package com.example.editor.engine

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.example.editor.model.AspectRatioPreset
import com.example.editor.model.BackgroundColorOption
import com.example.editor.model.EditorState
import com.example.editor.model.FilterType
import com.example.editor.model.FitMode
import com.example.editor.model.ImageAdjustments
import com.example.editor.model.ImageMetadata
import com.example.editor.model.NormalizedCropRect
import com.example.editor.model.OutputFormat
import com.example.editor.model.TargetSizeResult
import com.example.editor.model.TargetSizeStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object ImageEngine {

    /**
     * Loads a Bitmap from a content URI with memory safety (caps at 3000px max dimension).
     */
    suspend fun loadBitmapFromUri(context: Context, uri: Uri): Pair<Bitmap, ImageMetadata>? =
        withContext(Dispatchers.IO) {
            try {
                val resolver = context.contentResolver

                // 1. Query file metadata
                var filename = "selected_image"
                var fileSize = 0L
                resolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) filename = cursor.getString(nameIndex) ?: filename
                        if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                    }
                }

                val mimeType = resolver.getType(uri) ?: "image/jpeg"

                // 2. Read dimensions with inJustDecodeBounds
                var input: InputStream? = resolver.openInputStream(uri)
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(input, null, options)
                input?.close()

                val origW = options.outWidth
                val origH = options.outHeight

                if (origW <= 0 || origH <= 0) return@withContext null

                // Compute sample size for safe decoding
                val maxDim = max(origW, origH)
                var sampleSize = 1
                while (maxDim / sampleSize > 3000) {
                    sampleSize *= 2
                }

                // 3. Decode actual bitmap
                val decodeOptions = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                input = resolver.openInputStream(uri)
                val decodedBitmap = BitmapFactory.decodeStream(input, null, decodeOptions)
                input?.close()

                if (decodedBitmap == null) return@withContext null

                if (fileSize <= 0) {
                    fileSize = (decodedBitmap.byteCount * 0.45).toLong()
                }

                val metadata = ImageMetadata(
                    width = decodedBitmap.width,
                    height = decodedBitmap.height,
                    fileSizeBytes = fileSize,
                    mimeType = mimeType,
                    colorDepth = if (decodedBitmap.hasAlpha()) "32-bit ARGB" else "24-bit RGB",
                    qualityRating = if (decodedBitmap.width >= 1000) "High Quality" else "Standard Quality",
                    filename = filename
                )

                Pair(decodedBitmap, metadata)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    /**
     * Generates a sample candidate photo for quick testing without requiring local device files.
     */
    fun generateSampleCandidatePhoto(width: Int = 600, height: Int = 800): Pair<Bitmap, ImageMetadata> {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background: Soft light gradient / studio blue-grey
        val bgPaint = Paint().apply {
            color = Color.rgb(226, 232, 240) // Slate-200
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Subtle studio radial spotlight
        val spotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(241, 245, 249)
        }
        canvas.drawCircle(width / 2f, height * 0.4f, width * 0.45f, spotPaint)

        // Candidate silhouette / portrait
        val skinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(217, 163, 126)
        }
        val hairPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 27, 24)
        }
        val suitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(15, 30, 54) // DgNavyPrimary
        }
        val shirtPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(255, 255, 255)
        }
        val tiePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(234, 88, 12) // DgSaffron
        }

        // Shoulders / Coat
        val shoulderPath = android.graphics.Path().apply {
            moveTo(0f, height.toFloat())
            lineTo(0f, height * 0.72f)
            quadTo(width * 0.25f, height * 0.62f, width * 0.35f, height * 0.64f)
            lineTo(width * 0.5f, height * 0.85f)
            lineTo(width * 0.65f, height * 0.64f)
            quadTo(width * 0.75f, height * 0.62f, width.toFloat(), height * 0.72f)
            lineTo(width.toFloat(), height.toFloat())
            close()
        }
        canvas.drawPath(shoulderPath, suitPaint)

        // White formal collar
        val collarPath = android.graphics.Path().apply {
            moveTo(width * 0.38f, height * 0.58f)
            lineTo(width * 0.5f, height * 0.75f)
            lineTo(width * 0.62f, height * 0.58f)
            lineTo(width * 0.56f, height * 0.54f)
            lineTo(width * 0.44f, height * 0.54f)
            close()
        }
        canvas.drawPath(collarPath, shirtPaint)

        // Tie
        val tiePath = android.graphics.Path().apply {
            moveTo(width * 0.48f, height * 0.65f)
            lineTo(width * 0.52f, height * 0.65f)
            lineTo(width * 0.54f, height * 0.82f)
            lineTo(width * 0.50f, height * 0.88f)
            lineTo(width * 0.46f, height * 0.82f)
            close()
        }
        canvas.drawPath(tiePath, tiePaint)

        // Neck
        val neckPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(202, 148, 112)
        }
        canvas.drawRect(width * 0.42f, height * 0.50f, width * 0.58f, height * 0.62f, neckPaint)

        // Head / Face
        canvas.drawOval(
            RectF(width * 0.30f, height * 0.25f, width * 0.70f, height * 0.56f),
            skinPaint
        )

        // Hair
        val hairPath = android.graphics.Path().apply {
            moveTo(width * 0.28f, height * 0.35f)
            quadTo(width * 0.30f, height * 0.20f, width * 0.50f, height * 0.18f)
            quadTo(width * 0.70f, height * 0.20f, width * 0.72f, height * 0.35f)
            quadTo(width * 0.65f, height * 0.25f, width * 0.50f, height * 0.24f)
            quadTo(width * 0.35f, height * 0.25f, width * 0.28f, height * 0.35f)
            close()
        }
        canvas.drawPath(hairPath, hairPaint)

        // Candidate photo sample badge stamp
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(100, 116, 139)
            textSize = 20f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
        canvas.drawText("SAMPLE CANDIDATE PHOTO", width / 2f, height * 0.96f, textPaint)

        val metadata = ImageMetadata(
            width = width,
            height = height,
            fileSizeBytes = 245000L, // ~240 KB
            mimeType = "image/jpeg",
            colorDepth = "24-bit RGB",
            qualityRating = "Studio Preset (600x800)",
            filename = "Sample_Candidate_Passport.jpg"
        )
        return Pair(bitmap, metadata)
    }

    /**
     * Applies full editing transformations:
     * 1. Crop
     * 2. Rotation & Flip
     * 3. Sizing (Fit, Fill, Custom)
     * 4. Background Fill
     * 5. Color adjustments and filters
     */
    suspend fun applyTransformations(
        source: Bitmap,
        state: EditorState
    ): Bitmap = withContext(Dispatchers.Default) {
        // Step 1: Normalized Crop
        val cropRect = state.cropRect
        val sourceW = source.width
        val sourceH = source.height

        val cropX = (cropRect.left * sourceW).roundToInt().coerceIn(0, sourceW - 1)
        val cropY = (cropRect.top * sourceH).roundToInt().coerceIn(0, sourceH - 1)
        val cropW = ((cropRect.right - cropRect.left) * sourceW).roundToInt().coerceIn(1, sourceW - cropX)
        val cropH = ((cropRect.bottom - cropRect.top) * sourceH).roundToInt().coerceIn(1, sourceH - cropY)

        val croppedBitmap = if (cropX == 0 && cropY == 0 && cropW == sourceW && cropH == sourceH) {
            source
        } else {
            Bitmap.createBitmap(source, cropX, cropY, cropW, cropH)
        }

        // Step 2: Rotation and Flip
        val matrix = Matrix()
        if (state.isFlippedHorizontal) matrix.postScale(-1f, 1f)
        if (state.isFlippedVertical) matrix.postScale(1f, -1f)
        if (state.rotationDegrees != 0) matrix.postRotate(state.rotationDegrees.toFloat())

        val rotatedBitmap = if (!matrix.isIdentity) {
            Bitmap.createBitmap(
                croppedBitmap,
                0,
                0,
                croppedBitmap.width,
                croppedBitmap.height,
                matrix,
                true
            )
        } else {
            croppedBitmap
        }

        // Step 3: Compute final dimensions based on targetWidth/targetHeight and fitMode
        val destWidth = if (state.targetWidth > 0) state.targetWidth else rotatedBitmap.width
        val destHeight = if (state.targetHeight > 0) state.targetHeight else rotatedBitmap.height

        val outputBitmap = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)

        // Step 4: Background rendering
        when (state.backgroundColor) {
            BackgroundColorOption.WHITE -> canvas.drawColor(Color.WHITE)
            BackgroundColorOption.BLACK -> canvas.drawColor(Color.BLACK)
            BackgroundColorOption.TRANSPARENT -> canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            BackgroundColorOption.ORIGINAL -> {
                // If output format does not support transparency (like JPG), default canvas to white
                if (!state.outputFormat.supportsTransparency) {
                    canvas.drawColor(Color.WHITE)
                }
            }
        }

        // Step 5: Fit / Fill / Crop rendering onto destination canvas
        val srcW = rotatedBitmap.width.toFloat()
        val srcH = rotatedBitmap.height.toFloat()
        val dstW = destWidth.toFloat()
        val dstH = destHeight.toFloat()

        val destRect: RectF = when (state.fitMode) {
            FitMode.FIT -> {
                val scale = min(dstW / srcW, dstH / srcH)
                val scaledW = srcW * scale
                val scaledH = srcH * scale
                val left = (dstW - scaledW) / 2f
                val top = (dstH - scaledH) / 2f
                RectF(left, top, left + scaledW, top + scaledH)
            }
            FitMode.FILL -> {
                val scale = max(dstW / srcW, dstH / srcH)
                val scaledW = srcW * scale
                val scaledH = srcH * scale
                val left = (dstW - scaledW) / 2f
                val top = (dstH - scaledH) / 2f
                RectF(left, top, left + scaledW, top + scaledH)
            }
            FitMode.CROP -> {
                RectF(0f, 0f, dstW, dstH)
            }
        }

        // Paint with ColorMatrix adjustments and filters
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            colorFilter = buildCompositeColorFilter(state.adjustments, state.filter)
            alpha = ((state.adjustments.opacity / 100f) * 255).roundToInt().coerceIn(0, 255)
        }

        canvas.drawBitmap(rotatedBitmap, null, destRect, paint)

        // Step 6: Post-processing for Blur or Sharpen if required
        val finalResult = applyBlurOrSharpen(outputBitmap, state.adjustments.blur, state.adjustments.sharpen)

        finalResult
    }

    /**
     * Builds a comprehensive ColorMatrixFilter combining adjustments & preset filters.
     */
    private fun buildCompositeColorFilter(
        adjustments: ImageAdjustments,
        filter: FilterType
    ): ColorMatrixColorFilter {
        val composite = ColorMatrix()

        // 1. Base preset filter matrix
        val filterMatrix = when (filter) {
            FilterType.ORIGINAL -> ColorMatrix()
            FilterType.GRAYSCALE -> ColorMatrix().apply { setSaturation(0f) }
            FilterType.VINTAGE -> {
                val m = ColorMatrix()
                m.set(
                    floatArrayOf(
                        0.9f, 0f, 0f, 0f, 30f,
                        0f, 0.8f, 0f, 0f, 15f,
                        0f, 0f, 0.6f, 0f, 5f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                m
            }
            FilterType.WARM -> {
                val m = ColorMatrix()
                m.set(
                    floatArrayOf(
                        1.15f, 0f, 0f, 0f, 10f,
                        0f, 1.05f, 0f, 0f, 5f,
                        0f, 0f, 0.85f, 0f, -5f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                m
            }
            FilterType.COOL -> {
                val m = ColorMatrix()
                m.set(
                    floatArrayOf(
                        0.85f, 0f, 0f, 0f, -5f,
                        0f, 0.95f, 0f, 0f, 0f,
                        0f, 0f, 1.20f, 0f, 15f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                m
            }
            FilterType.BRIGHT -> {
                val m = ColorMatrix()
                val scale = 1.25f
                m.setScale(scale, scale, scale, 1f)
                m
            }
            FilterType.HIGH_CONTRAST -> {
                val m = ColorMatrix()
                val contrast = 1.4f
                val translate = (-0.5f * contrast + 0.5f) * 255f
                m.set(
                    floatArrayOf(
                        contrast, 0f, 0f, 0f, translate,
                        0f, contrast, 0f, 0f, translate,
                        0f, 0f, contrast, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                m
            }
            FilterType.SOFT -> {
                val m = ColorMatrix()
                val contrast = 0.88f
                val translate = (-0.5f * contrast + 0.5f) * 255f + 10f
                m.set(
                    floatArrayOf(
                        contrast, 0f, 0f, 0f, translate,
                        0f, contrast, 0f, 0f, translate,
                        0f, 0f, contrast, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                m
            }
        }
        composite.postConcat(filterMatrix)

        // 2. Brightness (-100 to +100 -> translate -100 to +100)
        if (adjustments.brightness != 0) {
            val b = adjustments.brightness.toFloat()
            val bMatrix = ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, b,
                    0f, 1f, 0f, 0f, b,
                    0f, 0f, 1f, 0f, b,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            composite.postConcat(bMatrix)
        }

        // 3. Contrast (-100 to +100)
        if (adjustments.contrast != 0) {
            val c = (100f + adjustments.contrast) / 100f
            val t = (-0.5f * c + 0.5f) * 255f
            val cMatrix = ColorMatrix(
                floatArrayOf(
                    c, 0f, 0f, 0f, t,
                    0f, c, 0f, 0f, t,
                    0f, 0f, c, 0f, t,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            composite.postConcat(cMatrix)
        }

        // 4. Saturation (-100 to +100)
        if (adjustments.saturation != 0) {
            val sat = (100f + adjustments.saturation) / 100f
            val sMatrix = ColorMatrix().apply { setSaturation(sat.coerceAtLeast(0f)) }
            composite.postConcat(sMatrix)
        }

        // 5. Grayscale slider (0 to 100)
        if (adjustments.grayscale > 0) {
            val grayFactor = adjustments.grayscale / 100f
            val sat = (1f - grayFactor).coerceIn(0f, 1f)
            val gMatrix = ColorMatrix().apply { setSaturation(sat) }
            composite.postConcat(gMatrix)
        }

        return ColorMatrixColorFilter(composite)
    }

    /**
     * Applies lightweight blur or unsharp mask sharpening.
     */
    private fun applyBlurOrSharpen(bitmap: Bitmap, blur: Int, sharpen: Int): Bitmap {
        if (blur <= 0 && sharpen <= 0) return bitmap

        val width = bitmap.width
        val height = bitmap.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        if (blur > 0) {
            // Fast downsample and upsample box blur approximation
            val radius = blur.coerceIn(1, 20)
            val downScale = max(1, radius / 2)
            val downW = max(1, width / downScale)
            val downH = max(1, height / downScale)

            val downBitmap = Bitmap.createScaledBitmap(bitmap, downW, downH, true)
            val blurPaint = Paint(Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(downBitmap, Rect(0, 0, downW, downH), Rect(0, 0, width, height), blurPaint)
            downBitmap.recycle()
            return output
        }

        if (sharpen > 0) {
            // Sharpen high-pass blend
            val sharpPaint = Paint(Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(bitmap, 0f, 0f, sharpPaint)

            // High pass accent
            val intensity = (sharpen / 100f) * 0.35f
            val blendPaint = Paint().apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
                alpha = (intensity * 255).roundToInt()
            }
            canvas.drawBitmap(bitmap, 0f, 0f, blendPaint)
            return output
        }

        return bitmap
    }

    /**
     * Compresses bitmap to raw bytes according to output format and quality.
     */
    suspend fun compressBitmap(
        bitmap: Bitmap,
        format: OutputFormat,
        quality: Int
    ): ByteArray = withContext(Dispatchers.Default) {
        val stream = ByteArrayOutputStream()
        val compressFormat = when (format) {
            OutputFormat.JPG, OutputFormat.JPEG -> Bitmap.CompressFormat.JPEG
            OutputFormat.PNG -> Bitmap.CompressFormat.PNG
            OutputFormat.WEBP -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    @Suppress("DEPRECATION")
                    Bitmap.CompressFormat.WEBP
                }
            }
        }
        val clampedQuality = quality.coerceIn(1, 100)
        bitmap.compress(compressFormat, clampedQuality, stream)
        stream.toByteArray()
    }

    /**
     * Iteratively finds optimal compression quality to target requested file size in KB.
     */
    suspend fun optimizeForTargetSize(
        bitmap: Bitmap,
        format: OutputFormat,
        targetKb: Int
    ): TargetSizeResult = withContext(Dispatchers.Default) {
        val targetBytes = targetKb * 1024L

        // PNG is lossless, quality parameter has no effect on size
        if (format == OutputFormat.PNG) {
            val bytes = compressBitmap(bitmap, format, 100)
            val actualKb = bytes.size / 1024f
            val status = when {
                bytes.size <= targetBytes -> TargetSizeStatus.PASS
                bytes.size <= targetBytes * 1.15f -> TargetSizeStatus.CLOSE
                else -> TargetSizeStatus.ABOVE_TARGET
            }
            return@withContext TargetSizeResult(
                targetKb = targetKb,
                actualKb = actualKb,
                actualBytes = bytes.size.toLong(),
                status = status,
                qualityUsed = 100
            )
        }

        // Binary search for optimal quality (1 to 100)
        var lowQuality = 5
        var highQuality = 100
        var bestQuality = 80
        var bestBytes = compressBitmap(bitmap, format, bestQuality)

        for (iter in 0 until 7) {
            val midQuality = (lowQuality + highQuality) / 2
            val midBytes = compressBitmap(bitmap, format, midQuality)
            val currentSize = midBytes.size

            bestQuality = midQuality
            bestBytes = midBytes

            if (abs(currentSize - targetBytes) < (targetBytes * 0.05f)) {
                // Within 5% of target, stop
                break
            }

            if (currentSize > targetBytes) {
                highQuality = midQuality - 1
            } else {
                lowQuality = midQuality + 1
            }

            if (lowQuality > highQuality) break
        }

        val actualKb = bestBytes.size / 1024f
        val status = when {
            bestBytes.size <= targetBytes -> TargetSizeStatus.PASS
            bestBytes.size <= targetBytes * 1.15f -> TargetSizeStatus.CLOSE
            else -> TargetSizeStatus.ABOVE_TARGET
        }

        TargetSizeResult(
            targetKb = targetKb,
            actualKb = actualKb,
            actualBytes = bestBytes.size.toLong(),
            status = status,
            qualityUsed = bestQuality
        )
    }

    /**
     * Saves byte array directly to Android device Pictures or Downloads folder via MediaStore.
     */
    suspend fun savePhotoToDevice(
        context: Context,
        bytes: ByteArray,
        format: OutputFormat,
        suggestedName: String = "DG_with_Anup_Edited_Photo"
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            val filename = "${suggestedName}_${System.currentTimeMillis()}.${format.extension}"
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, format.mimeType)
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
