package com.example.editor.validation

import com.example.editor.model.AspectRatioPreset
import com.example.editor.model.BackgroundColorOption
import com.example.editor.model.EditorState
import com.example.editor.model.OutputFormat
import com.example.editor.model.ValidationItem
import com.example.editor.model.ValidationReport
import kotlin.math.abs

object ValidationEngine {

    /**
     * Evaluates live validation status for current editor state and file byte size.
     * Reusable across Photo Editor and Government Job Photo & Signature Maker.
     */
    fun validate(
        state: EditorState,
        actualFileSizeBytes: Long,
        maxFileSizeBytes: Long? = null
    ): ValidationReport {
        // 1. Dimensions Check
        val width = if (state.targetWidth > 0) state.targetWidth else state.originalWidth
        val height = if (state.targetHeight > 0) state.targetHeight else state.originalHeight

        val dimValid = width in 1..8000 && height in 1..8000
        val dimMessage = if (dimValid) {
            "${width} × ${height} px (Valid size)"
        } else {
            "Invalid dimensions: minimum 1px required"
        }

        // 2. Aspect Ratio Check
        val currentRatio = if (height > 0) width.toFloat() / height.toFloat() else 1f
        val preset = state.selectedAspectRatioPreset

        val ratioValid: Boolean
        val ratioMessage: String

        if (preset == AspectRatioPreset.ORIGINAL || preset == AspectRatioPreset.CUSTOM) {
            ratioValid = true
            ratioMessage = "${String.format("%.2f", currentRatio)}:1 (${preset.label})"
        } else {
            val expectedRatio = preset.calculateAspectRatio(width, height)
            val diff = abs(currentRatio - expectedRatio)
            ratioValid = diff < 0.05f
            ratioMessage = if (ratioValid) {
                "${preset.label} matches target ratio"
            } else {
                "Expected ${preset.label} (${String.format("%.2f", expectedRatio)}:1), currently ${String.format("%.2f", currentRatio)}:1"
            }
        }

        // 3. Format Check
        val formatValid = state.outputFormat in OutputFormat.values()
        val formatMessage = when (state.outputFormat) {
            OutputFormat.JPG, OutputFormat.JPEG -> "JPG/JPEG (High compatibility for government forms)"
            OutputFormat.PNG -> "PNG (Lossless with transparency support)"
            OutputFormat.WEBP -> "WebP (Modern compact compression)"
        }

        // 4. File Size Check
        val actualKb = actualFileSizeBytes / 1024f
        val sizeValid: Boolean
        val sizeMessage: String

        if (state.isTargetSizeEnabled) {
            val targetKb = state.targetSizeKb
            sizeValid = actualKb <= targetKb * 1.05f // Allow 5% margin
            sizeMessage = if (sizeValid) {
                "${String.format("%.1f", actualKb)} KB (Under target ${targetKb} KB)"
            } else {
                "${String.format("%.1f", actualKb)} KB exceeds target ${targetKb} KB"
            }
        } else if (maxFileSizeBytes != null) {
            val maxKb = maxFileSizeBytes / 1024f
            sizeValid = actualKb <= maxKb
            sizeMessage = if (sizeValid) {
                "${String.format("%.1f", actualKb)} KB (Under limit ${maxKb.toInt()} KB)"
            } else {
                "${String.format("%.1f", actualKb)} KB exceeds limit ${maxKb.toInt()} KB"
            }
        } else {
            sizeValid = actualFileSizeBytes in 1..25_000_000 // Under 25MB
            sizeMessage = "${String.format("%.1f", actualKb)} KB (Within limits)"
        }

        // 5. Image Quality Check
        val qualityValid = state.quality in 1..100
        val qualityMessage = if (state.outputFormat == OutputFormat.PNG) {
            "100% Lossless (PNG compression)"
        } else {
            "${state.quality}% Quality (${if (state.quality >= 70) "Crisp" else if (state.quality >= 40) "Standard" else "Compressed"})"
        }

        // 6. Background Check
        val bgValid = !(state.backgroundColor == BackgroundColorOption.TRANSPARENT &&
                !state.outputFormat.supportsTransparency)
        val bgMessage = if (bgValid) {
            if (state.backgroundColor == BackgroundColorOption.TRANSPARENT) {
                "Transparent background with PNG/WebP"
            } else {
                "${state.backgroundColor.label} background configured"
            }
        } else {
            "JPG does not support transparency; will composite on white automatically"
        }

        return ValidationReport(
            dimensions = ValidationItem("Dimensions", dimValid, dimMessage),
            aspectRatio = ValidationItem("Aspect Ratio", ratioValid, ratioMessage),
            format = ValidationItem("Format", formatValid, formatMessage),
            fileSize = ValidationItem("File Size", sizeValid, sizeMessage),
            imageQuality = ValidationItem("Image Quality", qualityValid, qualityMessage),
            background = ValidationItem("Background", bgValid, bgMessage)
        )
    }
}
