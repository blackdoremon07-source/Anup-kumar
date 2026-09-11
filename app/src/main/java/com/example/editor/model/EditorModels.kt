package com.example.editor.model

import android.graphics.RectF

enum class OutputFormat(val extension: String, val mimeType: String, val supportsTransparency: Boolean) {
    JPG("jpg", "image/jpeg", false),
    JPEG("jpeg", "image/jpeg", false),
    PNG("png", "image/png", true),
    WEBP("webp", "image/webp", true)
}

enum class AspectRatioPreset(val label: String, val ratioWidth: Float?, val ratioHeight: Float?) {
    ORIGINAL("Original", null, null),
    RATIO_1_1("1:1 (Square)", 1f, 1f),
    RATIO_4_3("4:3", 4f, 3f),
    RATIO_3_4("3:4 (Passport Std)", 3f, 4f),
    RATIO_3_2("3:2", 3f, 2f),
    RATIO_2_3("2:3", 2f, 3f),
    RATIO_16_9("16:9", 16f, 9f),
    RATIO_9_16("9:16", 9f, 16f),
    CUSTOM("Custom", null, null);

    fun calculateAspectRatio(originalWidth: Int, originalHeight: Int): Float {
        return if (ratioWidth != null && ratioHeight != null) {
            ratioWidth / ratioHeight
        } else {
            if (originalHeight > 0) originalWidth.toFloat() / originalHeight else 1f
        }
    }
}

enum class FitMode(val title: String, val description: String) {
    FIT("Fit", "Entire image remains fully visible within bounds, adding clean padding if necessary."),
    FILL("Fill", "Container is completely filled, cropping excess edges seamlessly."),
    CROP("Crop", "User controls the exact visible area and framing.")
}

enum class FilterType(val displayName: String) {
    ORIGINAL("Original"),
    GRAYSCALE("Grayscale"),
    VINTAGE("Vintage"),
    WARM("Warm"),
    COOL("Cool"),
    BRIGHT("Bright"),
    HIGH_CONTRAST("High Contrast"),
    SOFT("Soft")
}

enum class BackgroundColorOption(val label: String, val hexColor: Long?) {
    ORIGINAL("Original", null),
    WHITE("White", 0xFFFFFFFF),
    BLACK("Black", 0xFF000000),
    TRANSPARENT("Transparent", 0x00000000)
}

data class ImageAdjustments(
    val brightness: Int = 0,    // -100 to +100
    val contrast: Int = 0,      // -100 to +100
    val saturation: Int = 0,    // -100 to +100
    val blur: Int = 0,          // 0 to 20
    val sharpen: Int = 0,       // 0 to 100
    val grayscale: Int = 0,     // 0 to 100
    val opacity: Int = 100      // 0 to 100
) {
    fun isDefault(): Boolean =
        brightness == 0 && contrast == 0 && saturation == 0 &&
                blur == 0 && sharpen == 0 && grayscale == 0 && opacity == 100
}

data class NormalizedCropRect(
    val left: Float = 0f,
    val top: Float = 0f,
    val right: Float = 1f,
    val bottom: Float = 1f
) {
    val width: Float get() = (right - left).coerceAtLeast(0.01f)
    val height: Float get() = (bottom - top).coerceAtLeast(0.01f)
    val aspectRatio: Float get() = width / height

    fun toAbsolute(imageWidth: Int, imageHeight: Int): RectF {
        return RectF(
            left * imageWidth,
            top * imageHeight,
            right * imageWidth,
            bottom * imageHeight
        )
    }

    companion object {
        val FULL = NormalizedCropRect(0f, 0f, 1f, 1f)
    }
}

data class ImageMetadata(
    val width: Int,
    val height: Int,
    val fileSizeBytes: Long,
    val mimeType: String,
    val colorDepth: String = "24-bit RGB (ARGB_8888)",
    val qualityRating: String = "High Resolution",
    val filename: String = "Selected_Photo"
)

enum class TargetSizeStatus {
    PASS,
    CLOSE,
    ABOVE_TARGET
}

data class TargetSizeResult(
    val targetKb: Int,
    val actualKb: Float,
    val actualBytes: Long,
    val status: TargetSizeStatus,
    val qualityUsed: Int
)

data class ValidationItem(
    val title: String,
    val isValid: Boolean,
    val message: String
)

data class ValidationReport(
    val dimensions: ValidationItem,
    val aspectRatio: ValidationItem,
    val format: ValidationItem,
    val fileSize: ValidationItem,
    val imageQuality: ValidationItem,
    val background: ValidationItem
) {
    val isAllValid: Boolean
        get() = dimensions.isValid && aspectRatio.isValid && format.isValid &&
                fileSize.isValid && imageQuality.isValid && background.isValid
}

data class EditorState(
    val originalWidth: Int = 0,
    val originalHeight: Int = 0,
    val targetWidth: Int = 0,
    val targetHeight: Int = 0,
    val isAspectLocked: Boolean = true,
    val selectedAspectRatioPreset: AspectRatioPreset = AspectRatioPreset.ORIGINAL,
    val cropRect: NormalizedCropRect = NormalizedCropRect.FULL,
    val fitMode: FitMode = FitMode.FIT,
    val rotationDegrees: Int = 0, // 0, 90, 180, 270
    val isFlippedHorizontal: Boolean = false,
    val isFlippedVertical: Boolean = false,
    val adjustments: ImageAdjustments = ImageAdjustments(),
    val filter: FilterType = FilterType.ORIGINAL,
    val backgroundColor: BackgroundColorOption = BackgroundColorOption.ORIGINAL,
    val outputFormat: OutputFormat = OutputFormat.JPG,
    val quality: Int = 90,
    val isTargetSizeEnabled: Boolean = false,
    val targetSizeKb: Int = 50,
    val resizePercentage: Int = 100
)
