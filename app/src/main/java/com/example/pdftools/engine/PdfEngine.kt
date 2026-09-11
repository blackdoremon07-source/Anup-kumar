package com.example.pdftools.engine

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import com.example.pdftools.model.PdfCompressionLevel
import com.example.pdftools.model.PdfCompressionResult
import com.example.pdftools.model.PdfFitMode
import com.example.pdftools.model.PdfImageItem
import com.example.pdftools.model.PdfPageMargin
import com.example.pdftools.model.PdfPageOrientation
import com.example.pdftools.model.PdfPageSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object PdfEngine {

    /**
     * Converts a list of [PdfImageItem] into a PDF document according to page layout settings.
     */
    suspend fun createPdfFromImages(
        images: List<PdfImageItem>,
        pageSize: PdfPageSize,
        orientation: PdfPageOrientation,
        margin: PdfPageMargin,
        fitMode: PdfFitMode
    ): ByteArray = withContext(Dispatchers.IO) {
        val document = PdfDocument()
        val marginPt = margin.points.toFloat()

        images.forEachIndexed { index, item ->
            // Apply rotation if needed
            val rotatedBitmap = if (item.rotationDegrees != 0) {
                val matrix = Matrix().apply { postRotate(item.rotationDegrees.toFloat()) }
                Bitmap.createBitmap(item.bitmap, 0, 0, item.bitmap.width, item.bitmap.height, matrix, true)
            } else {
                item.bitmap
            }

            // Determine page dimensions
            val (pageWidth, pageHeight) = when (pageSize) {
                PdfPageSize.FIT_IMAGE -> {
                    val w = rotatedBitmap.width + (marginPt * 2).toInt()
                    val h = rotatedBitmap.height + (marginPt * 2).toInt()
                    if (orientation == PdfPageOrientation.LANDSCAPE && h > w) Pair(h, w) else Pair(w, h)
                }
                else -> {
                    val baseW = pageSize.widthPt
                    val baseH = pageSize.heightPt
                    if (orientation == PdfPageOrientation.LANDSCAPE) Pair(baseH, baseW) else Pair(baseW, baseH)
                }
            }

            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            // Fill page with white background
            canvas.drawColor(Color.WHITE)

            // Calculate drawable area inside margins
            val targetRect = RectF(
                marginPt,
                marginPt,
                pageWidth.toFloat() - marginPt,
                pageHeight.toFloat() - marginPt
            )

            val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)

            when (fitMode) {
                PdfFitMode.FIT -> {
                    // Proportional fit inside targetRect
                    val scale = minOf(
                        targetRect.width() / rotatedBitmap.width.toFloat(),
                        targetRect.height() / rotatedBitmap.height.toFloat()
                    )
                    val drawW = rotatedBitmap.width * scale
                    val drawH = rotatedBitmap.height * scale
                    val left = targetRect.left + (targetRect.width() - drawW) / 2f
                    val top = targetRect.top + (targetRect.height() - drawH) / 2f
                    canvas.drawBitmap(rotatedBitmap, null, RectF(left, top, left + drawW, top + drawH), paint)
                }
                PdfFitMode.FILL -> {
                    // Fill bounds completely
                    canvas.drawBitmap(rotatedBitmap, null, targetRect, paint)
                }
            }

            document.finishPage(page)

            if (rotatedBitmap != item.bitmap && !rotatedBitmap.isRecycled) {
                rotatedBitmap.recycle()
            }
        }

        val out = ByteArrayOutputStream()
        document.writeTo(out)
        document.close()
        out.toByteArray()
    }

    /**
     * Extracts pages from a PDF file and renders them as Bitmaps.
     */
    suspend fun renderPdfPages(
        context: Context,
        pdfBytes: ByteArray,
        maxPages: Int = 100,
        renderScale: Float = 1.0f
    ): List<Bitmap> = withContext(Dispatchers.IO) {
        val bitmaps = mutableListOf<Bitmap>()
        val tempFile = File.createTempFile("pdf_render_", ".pdf", context.cacheDir)
        try {
            tempFile.writeBytes(pdfBytes)
            ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    val total = minOf(renderer.pageCount, maxPages)
                    for (i in 0 until total) {
                        val page = renderer.openPage(i)
                        val w = (page.width * renderScale).toInt().coerceAtLeast(1)
                        val h = (page.height * renderScale).toInt().coerceAtLeast(1)
                        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(bmp)
                        canvas.drawColor(Color.WHITE)
                        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        page.close()
                        bitmaps.add(bmp)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (tempFile.exists()) tempFile.delete()
        }
        bitmaps
    }

    /**
     * Inspects page count and dimensions of a PDF.
     */
    suspend fun getPdfPageCount(context: Context, pdfBytes: ByteArray): Int = withContext(Dispatchers.IO) {
        val tempFile = File.createTempFile("pdf_info_", ".pdf", context.cacheDir)
        var count = 0
        try {
            tempFile.writeBytes(pdfBytes)
            ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    count = renderer.pageCount
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (tempFile.exists()) tempFile.delete()
        }
        count
    }

    /**
     * Compresses a PDF by re-encoding pages at chosen JPEG quality and scale.
     */
    suspend fun compressPdf(
        context: Context,
        pdfBytes: ByteArray,
        level: PdfCompressionLevel
    ): PdfCompressionResult = withContext(Dispatchers.IO) {
        val originalSize = pdfBytes.size.toLong()
        val tempFile = File.createTempFile("pdf_comp_", ".pdf", context.cacheDir)
        val document = PdfDocument()

        try {
            tempFile.writeBytes(pdfBytes)
            ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    for (i in 0 until renderer.pageCount) {
                        val page = renderer.openPage(i)
                        val targetW = (page.width * level.scaleFactor).toInt().coerceAtLeast(100)
                        val targetH = (page.height * level.scaleFactor).toInt().coerceAtLeast(100)

                        val rawBitmap = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888)
                        val canvasRaw = Canvas(rawBitmap)
                        canvasRaw.drawColor(Color.WHITE)
                        page.render(rawBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                        page.close()

                        // Compress raw bitmap to JPEG in-memory
                        val jpegStream = ByteArrayOutputStream()
                        rawBitmap.compress(Bitmap.CompressFormat.JPEG, level.jpegQuality, jpegStream)
                        rawBitmap.recycle()

                        val compressedBytes = jpegStream.toByteArray()
                        val compressedBmp = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)

                        // Write compressed image onto target PDF page with original aspect
                        val pageInfo = PdfDocument.PageInfo.Builder(page.width, page.height, i + 1).create()
                        val outPage = document.startPage(pageInfo)
                        outPage.canvas.drawColor(Color.WHITE)
                        outPage.canvas.drawBitmap(
                            compressedBmp,
                            null,
                            RectF(0f, 0f, page.width.toFloat(), page.height.toFloat()),
                            Paint(Paint.FILTER_BITMAP_FLAG)
                        )
                        document.finishPage(outPage)
                        compressedBmp.recycle()
                    }
                }
            }

            val outStream = ByteArrayOutputStream()
            document.writeTo(outStream)
            document.close()
            val outputBytes = outStream.toByteArray()
            val outputSize = outputBytes.size.toLong()

            val explanation = when {
                outputSize < originalSize -> {
                    val percent = (((originalSize - outputSize).toDouble() / originalSize.toDouble()) * 100).toInt()
                    "Successfully compressed by $percent% (from ${originalSize / 1024} KB to ${outputSize / 1024} KB)."
                }
                else -> {
                    "Original PDF already contains compact vector data or compressed streams. Output is ${outputSize / 1024} KB. Use High compression if lower size is required."
                }
            }

            PdfCompressionResult(
                originalSizeBytes = originalSize,
                outputSizeBytes = outputSize,
                outputBytes = outputBytes,
                compressionLevel = level,
                explanationNotice = explanation
            )
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            PdfCompressionResult(
                originalSizeBytes = originalSize,
                outputSizeBytes = originalSize,
                outputBytes = pdfBytes,
                compressionLevel = level,
                explanationNotice = "Compression process failed or file was invalid. Preserved original file."
            )
        } finally {
            if (tempFile.exists()) tempFile.delete()
        }
    }

    /**
     * Merges multiple PDF byte arrays into a single unified PDF.
     */
    suspend fun mergePdfs(
        context: Context,
        pdfList: List<ByteArray>
    ): ByteArray = withContext(Dispatchers.IO) {
        val document = PdfDocument()
        var globalPageIndex = 1

        pdfList.forEach { pdfBytes ->
            val tempFile = File.createTempFile("merge_src_", ".pdf", context.cacheDir)
            try {
                tempFile.writeBytes(pdfBytes)
                ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                    PdfRenderer(pfd).use { renderer ->
                        for (i in 0 until renderer.pageCount) {
                            val page = renderer.openPage(i)
                            val pageInfo = PdfDocument.PageInfo.Builder(page.width, page.height, globalPageIndex++).create()
                            val outPage = document.startPage(pageInfo)

                            val bmp = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                            val c = Canvas(bmp)
                            c.drawColor(Color.WHITE)
                            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                            page.close()

                            outPage.canvas.drawColor(Color.WHITE)
                            outPage.canvas.drawBitmap(
                                bmp,
                                null,
                                RectF(0f, 0f, page.width.toFloat(), page.height.toFloat()),
                                Paint(Paint.FILTER_BITMAP_FLAG)
                            )
                            document.finishPage(outPage)
                            bmp.recycle()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (tempFile.exists()) tempFile.delete()
            }
        }

        val out = ByteArrayOutputStream()
        document.writeTo(out)
        document.close()
        out.toByteArray()
    }

    /**
     * Extracts selected page indices from a PDF into a new PDF.
     */
    suspend fun splitPdf(
        context: Context,
        pdfBytes: ByteArray,
        selectedPageIndices: Set<Int>
    ): ByteArray = withContext(Dispatchers.IO) {
        val document = PdfDocument()
        val tempFile = File.createTempFile("split_src_", ".pdf", context.cacheDir)
        var outPageIndex = 1

        try {
            tempFile.writeBytes(pdfBytes)
            ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    for (i in 0 until renderer.pageCount) {
                        if (i in selectedPageIndices) {
                            val page = renderer.openPage(i)
                            val pageInfo = PdfDocument.PageInfo.Builder(page.width, page.height, outPageIndex++).create()
                            val outPage = document.startPage(pageInfo)

                            val bmp = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                            val c = Canvas(bmp)
                            c.drawColor(Color.WHITE)
                            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                            page.close()

                            outPage.canvas.drawColor(Color.WHITE)
                            outPage.canvas.drawBitmap(
                                bmp,
                                null,
                                RectF(0f, 0f, page.width.toFloat(), page.height.toFloat()),
                                Paint(Paint.FILTER_BITMAP_FLAG)
                            )
                            document.finishPage(outPage)
                            bmp.recycle()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (tempFile.exists()) tempFile.delete()
        }

        val out = ByteArrayOutputStream()
        document.writeTo(out)
        document.close()
        out.toByteArray()
    }

    /**
     * Reorders pages of a PDF based on the specified list of page indices.
     */
    suspend fun reorderPdf(
        context: Context,
        pdfBytes: ByteArray,
        orderedPageIndices: List<Int>
    ): ByteArray = withContext(Dispatchers.IO) {
        val document = PdfDocument()
        val tempFile = File.createTempFile("reorder_src_", ".pdf", context.cacheDir)

        try {
            tempFile.writeBytes(pdfBytes)
            ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    orderedPageIndices.forEachIndexed { newIndex, originalPageIndex ->
                        if (originalPageIndex in 0 until renderer.pageCount) {
                            val page = renderer.openPage(originalPageIndex)
                            val pageInfo = PdfDocument.PageInfo.Builder(page.width, page.height, newIndex + 1).create()
                            val outPage = document.startPage(pageInfo)

                            val bmp = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                            val c = Canvas(bmp)
                            c.drawColor(Color.WHITE)
                            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                            page.close()

                            outPage.canvas.drawColor(Color.WHITE)
                            outPage.canvas.drawBitmap(
                                bmp,
                                null,
                                RectF(0f, 0f, page.width.toFloat(), page.height.toFloat()),
                                Paint(Paint.FILTER_BITMAP_FLAG)
                            )
                            document.finishPage(outPage)
                            bmp.recycle()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (tempFile.exists()) tempFile.delete()
        }

        val out = ByteArrayOutputStream()
        document.writeTo(out)
        document.close()
        out.toByteArray()
    }

    /**
     * Converts all pages of a PDF into JPG or PNG image files and packages them into a ZIP.
     */
    suspend fun convertPdfToImagesZip(
        context: Context,
        pdfBytes: ByteArray,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        fileExtension: String = "jpg"
    ): ByteArray = withContext(Dispatchers.IO) {
        val pages = renderPdfPages(context, pdfBytes, maxPages = 50, renderScale = 1.5f)
        val zipStream = ByteArrayOutputStream()
        ZipOutputStream(zipStream).use { zos ->
            pages.forEachIndexed { index, bitmap ->
                val entry = ZipEntry("DG_Page_${index + 1}.$fileExtension")
                zos.putNextEntry(entry)
                val imgStream = ByteArrayOutputStream()
                bitmap.compress(format, 90, imgStream)
                zos.write(imgStream.toByteArray())
                zos.closeEntry()
                bitmap.recycle()
            }
        }
        zipStream.toByteArray()
    }

    /**
     * Saves byte array to device storage (Downloads or Documents) with MediaStore or File.
     */
    suspend fun saveFileToStorage(
        context: Context,
        bytes: ByteArray,
        fileName: String,
        mimeType: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return@withContext Result.failure(Exception("Failed to create download URI"))

                context.contentResolver.openOutputStream(uri)?.use { os ->
                    os.write(bytes)
                }
                Result.success("Saved to Downloads/$fileName")
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val targetFile = File(downloadsDir, fileName)
                FileOutputStream(targetFile).use { it.write(bytes) }
                Result.success("Saved to ${targetFile.absolutePath}")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
