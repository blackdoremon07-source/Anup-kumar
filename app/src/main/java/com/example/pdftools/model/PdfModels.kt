package com.example.pdftools.model

import android.graphics.Bitmap
import android.net.Uri

enum class PdfToolType(val title: String, val description: String) {
    IMAGE_TO_PDF("Image to PDF", "Convert JPG, PNG & WebP images into a single PDF document"),
    COMPRESSOR("PDF Compressor", "Reduce file size for portal uploads with custom compression levels"),
    MERGE("Merge PDF", "Combine multiple PDF certificates and documents into one file"),
    SPLIT("Split PDF", "Extract specific pages or page ranges into a separate PDF"),
    REORDER("Page Reorder", "Rearrange and reorder pages of a PDF document"),
    PDF_TO_IMAGES("PDF to Images", "Convert PDF document pages into high-resolution JPG/PNG images"),
    VIEWER("PDF Preview", "View pages, inspect file specs, zoom, and navigate document")
}

enum class PdfPageSize(val label: String, val widthPt: Int, val heightPt: Int) {
    A4("A4 (210 × 297 mm)", 595, 842),
    LETTER("US Letter (8.5 × 11 in)", 612, 792),
    FIT_IMAGE("Fit Image Size", 0, 0)
}

enum class PdfPageOrientation(val label: String) {
    PORTRAIT("Portrait"),
    LANDSCAPE("Landscape")
}

enum class PdfPageMargin(val label: String, val points: Int) {
    NONE("No Margin", 0),
    SMALL("Small (0.25 in)", 18),
    NORMAL("Normal (0.5 in)", 36),
    LARGE("Large (1.0 in)", 72)
}

enum class PdfFitMode(val label: String) {
    FIT("Fit (Keep Aspect Ratio)", ),
    FILL("Fill / Stretch Page")
}

enum class PdfCompressionLevel(
    val label: String,
    val description: String,
    val jpegQuality: Int,
    val scaleFactor: Float
) {
    LOW("Low Compression", "High quality, best for documents with fine text", 85, 1.0f),
    MEDIUM("Medium Compression", "Balanced quality and size, recommended for portal uploads", 65, 0.8f),
    HIGH("High Compression", "Maximum size reduction for strict < 200KB limits", 45, 0.6f)
}

data class PdfImageItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val uri: Uri? = null,
    val bitmap: Bitmap,
    val rotationDegrees: Int = 0,
    val originalWidth: Int = bitmap.width,
    val originalHeight: Int = bitmap.height,
    val fileName: String = "image_${System.currentTimeMillis()}"
)

data class PdfSourceFile(
    val id: String = java.util.UUID.randomUUID().toString(),
    val uri: Uri? = null,
    val fileName: String,
    val fileSizeBytes: Long,
    val pageCount: Int,
    val thumbnail: Bitmap? = null,
    val fileBytes: ByteArray
)

data class PdfPageThumbnail(
    val pageIndex: Int,
    val thumbnail: Bitmap,
    val isSelected: Boolean = true
)

data class PdfCompressionResult(
    val originalSizeBytes: Long,
    val outputSizeBytes: Long,
    val outputBytes: ByteArray,
    val compressionLevel: PdfCompressionLevel,
    val explanationNotice: String
) {
    val reductionPercentage: Int
        get() = if (originalSizeBytes > 0) {
            val diff = originalSizeBytes - outputSizeBytes
            ((diff.toDouble() / originalSizeBytes.toDouble()) * 100).toInt().coerceAtLeast(0)
        } else 0
}
