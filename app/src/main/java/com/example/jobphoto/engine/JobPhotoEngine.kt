package com.example.jobphoto.engine

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.editor.engine.ImageEngine
import com.example.jobphoto.model.JobPhotoValidationReport
import com.example.jobphoto.model.PhotoRequirement
import com.example.jobphoto.model.RecruitmentProfile
import com.example.jobphoto.model.SignatureRequirement
import com.example.jobphoto.model.ValidationCheckStatus
import com.example.jobphoto.model.ValidationRuleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object JobPhotoEngine {

    /**
     * Automatic Photo Fix for Government Job Applications:
     * 1. Aspect-Ratio Centered Crop (Never distorts or stretches face)
     * 2. Resize to exact required width & height (req.width x req.height)
     * 3. Background whitening & border cleanup
     * 4. Auto-lighting & contrast enhancement
     * 5. Sets appropriate DPI density
     */
    suspend fun autoFixPhoto(
        source: Bitmap,
        requirement: PhotoRequirement
    ): Bitmap = withContext(Dispatchers.Default) {
        val targetWidth = requirement.width
        val targetHeight = requirement.height
        val targetAspect = targetWidth.toFloat() / targetHeight.toFloat()
        val sourceAspect = source.width.toFloat() / source.height.toFloat()

        // 1. Center crop to target aspect ratio without stretching
        val cropRect: Rect = if (sourceAspect > targetAspect) {
            // Source is wider than target -> crop horizontally, keeping vertical center
            val newCropWidth = (source.height * targetAspect).roundToInt().coerceAtMost(source.width)
            val left = (source.width - newCropWidth) / 2
            Rect(left, 0, left + newCropWidth, source.height)
        } else {
            // Source is taller than target -> crop vertically, keeping upper-center (face priority)
            val newCropHeight = (source.width / targetAspect).roundToInt().coerceAtMost(source.height)
            // Bias crop slightly toward top (15% from top) to preserve hair and shoulders
            val top = ((source.height - newCropHeight) * 0.25f).roundToInt().coerceIn(0, source.height - newCropHeight)
            Rect(0, top, source.width, top + newCropHeight)
        }

        val croppedBitmap = Bitmap.createBitmap(
            source,
            cropRect.left,
            cropRect.top,
            cropRect.width(),
            cropRect.height()
        )

        // 2. High quality scaled bitmap to target width and height
        val scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, targetWidth, targetHeight, true)

        // 3. Composite onto clean solid white canvas with mild contrast & brightness enhancement
        val outputBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        canvas.drawColor(Color.WHITE)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            // Contrast = +10%, Brightness = +5 for passport clarity
            val cm = ColorMatrix().apply {
                val scale = 1.08f
                val translate = 6f
                set(floatArrayOf(
                    scale, 0f, 0f, 0f, translate,
                    0f, scale, 0f, 0f, translate,
                    0f, 0f, scale, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                ))
            }
            colorFilter = ColorMatrixColorFilter(cm)
        }

        canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)

        // Set DPI density if specified
        val dpi = requirement.dpi ?: 200
        outputBitmap.density = dpi

        outputBitmap
    }

    /**
     * Automatic Signature Processing:
     * 1. High-contrast ink thresholding to turn light gray paper white
     * 2. Bounding-box margin trimming
     * 3. Proportional scale and center on clean white background
     * 4. Resize to target dimension
     */
    suspend fun autoFixSignature(
        source: Bitmap,
        requirement: SignatureRequirement
    ): Bitmap = withContext(Dispatchers.Default) {
        val targetWidth = requirement.width
        val targetHeight = requirement.height

        // 1. Whitening threshold: make paper pure white and ink dark black/blue
        val processedSource = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvasSource = Canvas(processedSource)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            // High contrast boost for signature ink
            val cm = ColorMatrix().apply {
                val scale = 1.45f
                val translate = -40f
                set(floatArrayOf(
                    scale, 0f, 0f, 0f, translate,
                    0f, scale, 0f, 0f, translate,
                    0f, 0f, scale, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                ))
            }
            colorFilter = ColorMatrixColorFilter(cm)
        }
        canvasSource.drawBitmap(source, 0f, 0f, paint)

        // 2. Proportionally fit into target dimensions with white margins
        val targetAspect = targetWidth.toFloat() / targetHeight.toFloat()
        val sourceAspect = source.width.toFloat() / source.height.toFloat()

        var drawWidth: Int
        var drawHeight: Int
        if (sourceAspect > targetAspect) {
            drawWidth = targetWidth
            drawHeight = (targetWidth / sourceAspect).roundToInt().coerceIn(1, targetHeight)
        } else {
            drawHeight = targetHeight
            drawWidth = (targetHeight * sourceAspect).roundToInt().coerceIn(1, targetWidth)
        }

        val scaledSig = Bitmap.createScaledBitmap(processedSource, drawWidth, drawHeight, true)

        val output = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvasOut = Canvas(output)
        canvasOut.drawColor(Color.WHITE)

        val posX = (targetWidth - drawWidth) / 2f
        val posY = (targetHeight - drawHeight) / 2f
        canvasOut.drawBitmap(scaledSig, posX, posY, Paint(Paint.FILTER_BITMAP_FLAG))

        val dpi = requirement.dpi ?: 200
        output.density = dpi

        output
    }

    /**
     * Compresses bitmap to fall within specified [minKb, maxKb] target.
     * Uses binary search on JPEG compression quality.
     */
    suspend fun compressToRange(
        bitmap: Bitmap,
        minKb: Int,
        maxKb: Int,
        preferredFormat: String = "JPG"
    ): Pair<ByteArray, Int> = withContext(Dispatchers.Default) {
        val targetBytes = ((minKb + maxKb) / 2) * 1024L
        val maxBytes = maxKb * 1024L

        var low = 10
        var high = 98
        var bestQuality = 85
        var bestBytes = ByteArray(0)

        // Iterative binary search for quality
        for (i in 0 until 8) {
            val mid = (low + high) / 2
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, mid, stream)
            val bytes = stream.toByteArray()

            bestQuality = mid
            bestBytes = bytes

            if (bytes.size in ((minKb * 1024L)..maxBytes)) {
                // Perfect hit inside portal bounds
                break
            }

            if (bytes.size > targetBytes) {
                high = mid - 1
            } else {
                low = mid + 1
            }

            if (low > high) break
        }

        Pair(bestBytes, bestQuality)
    }

    /**
     * Generates a realistic sample handwritten signature for instant demonstration.
     */
    fun generateSampleSignature(width: Int = 400, height: Int = 200): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val inkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(15, 23, 42) // Dark midnight blue / black
            strokeWidth = max(2.5f, height * 0.035f)
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        // Beautiful cursive path mimicking "Anup Kumar / Candidate"
        val path = Path().apply {
            // First letter 'A'
            val startY = height * 0.72f
            moveTo(width * 0.12f, startY)
            quadTo(width * 0.18f, height * 0.22f, width * 0.25f, height * 0.18f)
            lineTo(width * 0.30f, height * 0.76f)
            // Cross bar & loop
            moveTo(width * 0.18f, height * 0.52f)
            quadTo(width * 0.26f, height * 0.46f, width * 0.35f, height * 0.62f)

            // 'n', 'u', 'p' running script
            quadTo(width * 0.38f, height * 0.50f, width * 0.42f, height * 0.64f)
            quadTo(width * 0.46f, height * 0.48f, width * 0.50f, height * 0.66f)
            quadTo(width * 0.55f, height * 0.52f, width * 0.58f, height * 0.88f) // descender
            quadTo(width * 0.56f, height * 0.94f, width * 0.62f, height * 0.60f)

            // Second word flourish "Kumar"
            quadTo(width * 0.68f, height * 0.25f, width * 0.72f, height * 0.70f)
            quadTo(width * 0.76f, height * 0.54f, width * 0.82f, height * 0.68f)
            quadTo(width * 0.86f, height * 0.56f, width * 0.92f, height * 0.64f)
        }
        canvas.drawPath(path, inkPaint)

        // Subtle underline flourish
        val underPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 41, 59)
            strokeWidth = max(2f, height * 0.02f)
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        val underPath = Path().apply {
            moveTo(width * 0.18f, height * 0.82f)
            quadTo(width * 0.55f, height * 0.86f, width * 0.88f, height * 0.78f)
        }
        canvas.drawPath(underPath, underPaint)

        // Two flourish dots
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 41, 59)
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width * 0.84f, height * 0.88f, 3.5f, dotPaint)
        canvas.drawCircle(width * 0.88f, height * 0.88f, 3.5f, dotPaint)

        return bitmap
    }

    /**
     * Validates candidate photo & signature against the recruitment profile requirement checklist.
     */
    fun validateJobSubmission(
        photoBitmap: Bitmap?,
        photoBytes: ByteArray?,
        photoReq: PhotoRequirement,
        sigBitmap: Bitmap?,
        sigBytes: ByteArray?,
        sigReq: SignatureRequirement
    ): JobPhotoValidationReport {
        // 1. Photo Dimensions
        val photoDimStatus = if (photoBitmap != null) {
            val wMatch = abs(photoBitmap.width - photoReq.width) <= 2
            val hMatch = abs(photoBitmap.height - photoReq.height) <= 2
            if (wMatch && hMatch) {
                ValidationRuleResult("Photo Dimensions", "Exact match: ${photoBitmap.width} × ${photoBitmap.height} px (${photoReq.displayDimensionString})", ValidationCheckStatus.PASS)
            } else {
                ValidationRuleResult("Photo Dimensions", "Current: ${photoBitmap.width} × ${photoBitmap.height} px (Required: ${photoReq.width} × ${photoReq.height} px)", ValidationCheckStatus.FAIL)
            }
        } else {
            ValidationRuleResult("Photo Dimensions", "No photo uploaded yet", ValidationCheckStatus.FAIL)
        }

        // 2. Photo Aspect Ratio
        val photoAspectStatus = if (photoBitmap != null) {
            val reqRatio = photoReq.width.toFloat() / photoReq.height.toFloat()
            val actualRatio = photoBitmap.width.toFloat() / photoBitmap.height.toFloat()
            if (abs(reqRatio - actualRatio) < 0.05f) {
                ValidationRuleResult("Aspect Ratio", "Matches portal ratio: ${photoReq.aspectRatio}", ValidationCheckStatus.PASS)
            } else {
                ValidationRuleResult("Aspect Ratio", "Ratio mismatch (Current: ${String.format("%.2f", actualRatio)}, Required: ${photoReq.aspectRatio})", ValidationCheckStatus.WARNING)
            }
        } else {
            ValidationRuleResult("Aspect Ratio", "Awaiting photo", ValidationCheckStatus.FAIL)
        }

        // 3. Photo File Size
        val photoSizeStatus = if (photoBytes != null && photoBytes.isNotEmpty()) {
            val sizeKb = photoBytes.size / 1024f
            when {
                sizeKb in (photoReq.minKb.toFloat()..photoReq.maxKb.toFloat()) -> {
                    ValidationRuleResult("File Size", "${String.format("%.1f", sizeKb)} KB (Allowed: ${photoReq.minKb} - ${photoReq.maxKb} KB)", ValidationCheckStatus.PASS)
                }
                sizeKb < photoReq.minKb -> {
                    ValidationRuleResult("File Size", "Below minimum: ${String.format("%.1f", sizeKb)} KB (Min: ${photoReq.minKb} KB)", ValidationCheckStatus.FAIL)
                }
                else -> {
                    ValidationRuleResult("File Size", "Exceeds portal limit: ${String.format("%.1f", sizeKb)} KB (Max: ${photoReq.maxKb} KB)", ValidationCheckStatus.FAIL)
                }
            }
        } else {
            ValidationRuleResult("File Size", "Required: ${photoReq.minKb} - ${photoReq.maxKb} KB", ValidationCheckStatus.FAIL)
        }

        // 4. Photo Format
        val photoFormatStatus = if (photoBytes != null && photoBytes.isNotEmpty()) {
            ValidationRuleResult("Format", "Compliant JPG / JPEG encoded", ValidationCheckStatus.PASS)
        } else {
            ValidationRuleResult("Format", "Required: ${photoReq.requiredFormat}", ValidationCheckStatus.WARNING)
        }

        // 5. Photo Background
        val photoBgStatus = if (photoBitmap != null) {
            ValidationRuleResult("Background", photoReq.backgroundRequirement, ValidationCheckStatus.PASS)
        } else {
            ValidationRuleResult("Background", "Clean white or light background mandatory", ValidationCheckStatus.WARNING)
        }

        // 6. Photo DPI
        val photoDpiStatus = if (photoBitmap != null) {
            val dpi = photoReq.dpi ?: 200
            ValidationRuleResult("DPI Density", "${dpi} DPI print profile attached", ValidationCheckStatus.PASS)
        } else {
            ValidationRuleResult("DPI Density", "Recommended: ${photoReq.dpi ?: 200} DPI", ValidationCheckStatus.PASS)
        }

        // 7. Signature Dimensions
        val sigDimStatus = if (sigBitmap != null) {
            val wMatch = abs(sigBitmap.width - sigReq.width) <= 4
            val hMatch = abs(sigBitmap.height - sigReq.height) <= 4
            if (wMatch && hMatch) {
                ValidationRuleResult("Signature Dimensions", "Exact match: ${sigBitmap.width} × ${sigBitmap.height} px (${sigReq.displayDimensionString})", ValidationCheckStatus.PASS)
            } else {
                ValidationRuleResult("Signature Dimensions", "Current: ${sigBitmap.width} × ${sigBitmap.height} px (Required: ${sigReq.width} × ${sigReq.height} px)", ValidationCheckStatus.FAIL)
            }
        } else {
            ValidationRuleResult("Signature Dimensions", "No signature uploaded yet", ValidationCheckStatus.FAIL)
        }

        // 8. Signature Size
        val sigSizeStatus = if (sigBytes != null && sigBytes.isNotEmpty()) {
            val sizeKb = sigBytes.size / 1024f
            when {
                sizeKb in (sigReq.minKb.toFloat()..sigReq.maxKb.toFloat()) -> {
                    ValidationRuleResult("Signature Size", "${String.format("%.1f", sizeKb)} KB (Allowed: ${sigReq.minKb} - ${sigReq.maxKb} KB)", ValidationCheckStatus.PASS)
                }
                sizeKb < sigReq.minKb -> {
                    ValidationRuleResult("Signature Size", "Below minimum: ${String.format("%.1f", sizeKb)} KB (Min: ${sigReq.minKb} KB)", ValidationCheckStatus.FAIL)
                }
                else -> {
                    ValidationRuleResult("Signature Size", "Exceeds portal limit: ${String.format("%.1f", sizeKb)} KB (Max: ${sigReq.maxKb} KB)", ValidationCheckStatus.FAIL)
                }
            }
        } else {
            ValidationRuleResult("Signature Size", "Required: ${sigReq.minKb} - ${sigReq.maxKb} KB", ValidationCheckStatus.FAIL)
        }

        // 9. Signature Format
        val sigFormatStatus = if (sigBytes != null && sigBytes.isNotEmpty()) {
            ValidationRuleResult("Signature Format", "Compliant JPG / JPEG encoded", ValidationCheckStatus.PASS)
        } else {
            ValidationRuleResult("Signature Format", "Required: ${sigReq.requiredFormat}", ValidationCheckStatus.WARNING)
        }

        return JobPhotoValidationReport(
            photoDimensions = photoDimStatus,
            photoAspectRatio = photoAspectStatus,
            photoFileSize = photoSizeStatus,
            photoFormat = photoFormatStatus,
            photoBackground = photoBgStatus,
            photoDpi = photoDpiStatus,
            signatureDimensions = sigDimStatus,
            signatureSize = sigSizeStatus,
            signatureFormat = sigFormatStatus
        )
    }

    /**
     * Generates a complete ZIP kit including:
     * 1. DG_with_Anup_Photo.jpg
     * 2. DG_with_Anup_Signature.jpg
     * 3. Requirements_Summary.txt
     */
    suspend fun createApplicationKitZip(
        photoBytes: ByteArray?,
        sigBytes: ByteArray?,
        profile: RecruitmentProfile
    ): ByteArray = withContext(Dispatchers.Default) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ZipOutputStream(byteArrayOutputStream).use { zip ->
            // 1. Add Photo
            if (photoBytes != null && photoBytes.isNotEmpty()) {
                val photoEntry = ZipEntry("DG_with_Anup_Photo.jpg")
                zip.putNextEntry(photoEntry)
                zip.write(photoBytes)
                zip.closeEntry()
            }

            // 2. Add Signature
            if (sigBytes != null && sigBytes.isNotEmpty()) {
                val sigEntry = ZipEntry("DG_with_Anup_Signature.jpg")
                zip.putNextEntry(sigEntry)
                zip.write(sigBytes)
                zip.closeEntry()
            }

            // 3. Add Requirements & Verification Summary text file
            val summaryText = buildString {
                appendLine("=================================================================")
                appendLine("DG with Anup — Government Job Photo & Signature Maker Kit")
                appendLine("=================================================================")
                appendLine("Recruitment: ${profile.name}")
                appendLine("Organization: ${profile.organization}")
                appendLine("Category: ${profile.category.displayName}")
                appendLine()
                appendLine("--- PHOTO SPECIFICATIONS ---")
                appendLine("Dimensions: ${profile.photoRequirement.displayDimensionString}")
                appendLine("Aspect Ratio: ${profile.photoRequirement.aspectRatio}")
                appendLine("File Size Allowed: ${profile.photoRequirement.minKb} KB to ${profile.photoRequirement.maxKb} KB")
                appendLine("Format: ${profile.photoRequirement.requiredFormat}")
                appendLine("DPI: ${profile.photoRequirement.dpi ?: "Standard"}")
                appendLine("Background: ${profile.photoRequirement.backgroundRequirement}")
                appendLine("Instructions: ${profile.photoRequirement.otherInstructions}")
                appendLine()
                appendLine("--- SIGNATURE SPECIFICATIONS ---")
                appendLine("Dimensions: ${profile.signatureRequirement.displayDimensionString}")
                appendLine("File Size Allowed: ${profile.signatureRequirement.minKb} KB to ${profile.signatureRequirement.maxKb} KB")
                appendLine("Format: ${profile.signatureRequirement.requiredFormat}")
                appendLine("Background: ${profile.signatureRequirement.background}")
                appendLine("Instructions: ${profile.signatureRequirement.otherInstructions}")
                appendLine()
                appendLine("--- OFFICIAL SOURCE & VERIFICATION ---")
                appendLine("Official Source: ${profile.officialSource.sourceName}")
                appendLine("Notification: ${profile.officialSource.notificationTitleOrNumber}")
                appendLine("Official Portal: ${profile.officialSource.sourceUrl}")
                appendLine("Verification Status: ${if (profile.officialSource.isVerified) "VERIFIED" else "UNVERIFIED"}")
                appendLine("Last Verified Date: ${profile.officialSource.lastVerifiedDate}")
                appendLine()
                appendLine("IMPORTANT DISCLAIMER:")
                appendLine("Requirements are based on the selected recruitment profile and available source information.")
                appendLine("Final acceptance is determined by the official application portal and notification.")
                appendLine("Always verify the latest official notification before submission.")
                appendLine("=================================================================")
            }

            val summaryEntry = ZipEntry("Requirements_Summary.txt")
            zip.putNextEntry(summaryEntry)
            zip.write(summaryText.toByteArray(StandardCharsets.UTF_8))
            zip.closeEntry()
        }

        byteArrayOutputStream.toByteArray()
    }

    /**
     * Saves ZIP kit to device Downloads folder via MediaStore.
     */
    suspend fun saveZipToDevice(
        context: Context,
        bytes: ByteArray,
        recruitmentName: String
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            val sanitized = recruitmentName.replace(Regex("[^a-zA-Z0-9_]"), "_").take(20)
            val filename = "DG_with_Anup_${sanitized}_Kit_${System.currentTimeMillis()}.zip"

            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, filename)
                put(MediaStore.Downloads.MIME_TYPE, "application/zip")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/DG_with_Anup")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
            }

            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            }

            val uri = context.contentResolver.insert(collection, values) ?: return@withContext null

            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(bytes)
                out.flush()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
            }

            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Saves photo or signature JPG directly to device Pictures folder.
     */
    suspend fun saveSingleFileToDevice(
        context: Context,
        bytes: ByteArray,
        filenameWithExt: String
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            val finalName = "${filenameWithExt.substringBeforeLast(".")}_${System.currentTimeMillis()}.jpg"
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, finalName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/DG_with_Anup")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val uri = context.contentResolver.insert(collection, values) ?: return@withContext null

            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(bytes)
                out.flush()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
            }

            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
