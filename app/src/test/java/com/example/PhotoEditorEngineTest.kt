package com.example

import com.example.editor.engine.ImageEngine
import com.example.editor.model.AspectRatioPreset
import com.example.editor.model.BackgroundColorOption
import com.example.editor.model.EditorState
import com.example.editor.model.FilterType
import com.example.editor.model.FitMode
import com.example.editor.model.ImageAdjustments
import com.example.editor.model.NormalizedCropRect
import com.example.editor.model.OutputFormat
import com.example.editor.validation.ValidationEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PhotoEditorEngineTest {

    @Test
    fun testSamplePhotoGeneration() {
        val (bitmap, metadata) = ImageEngine.generateSampleCandidatePhoto(300, 400)
        assertNotNull(bitmap)
        assertEquals(300, bitmap.width)
        assertEquals(400, bitmap.height)
        assertEquals(300, metadata.width)
        assertEquals(400, metadata.height)
    }

    @Test
    fun testAspectRatiosCalculations() {
        val ratio11 = AspectRatioPreset.RATIO_1_1.calculateAspectRatio(600, 800)
        assertEquals(1.0f, ratio11, 0.01f)

        val ratio34 = AspectRatioPreset.RATIO_3_4.calculateAspectRatio(600, 800)
        assertEquals(0.75f, ratio34, 0.01f)

        val ratio169 = AspectRatioPreset.RATIO_16_9.calculateAspectRatio(600, 800)
        assertEquals(16f / 9f, ratio169, 0.01f)
    }

    @Test
    fun testValidationEngine() {
        val state = EditorState(
            originalWidth = 600,
            originalHeight = 800,
            targetWidth = 300,
            targetHeight = 400,
            selectedAspectRatioPreset = AspectRatioPreset.RATIO_3_4,
            outputFormat = OutputFormat.JPG,
            quality = 85,
            isTargetSizeEnabled = true,
            targetSizeKb = 50
        )

        val report = ValidationEngine.validate(state, actualFileSizeBytes = 42 * 1024L)
        assertTrue(report.dimensions.isValid)
        assertTrue(report.aspectRatio.isValid)
        assertTrue(report.format.isValid)
        assertTrue(report.fileSize.isValid)
        assertTrue(report.imageQuality.isValid)
        assertTrue(report.background.isValid)
        assertTrue(report.isAllValid)
    }

    @Test
    fun testImageTransformationsAndCompression() = runBlocking {
        val (source, _) = ImageEngine.generateSampleCandidatePhoto(200, 200)

        // Rotate 90 deg and resize to 150x150
        val state = EditorState(
            originalWidth = 200,
            originalHeight = 200,
            targetWidth = 150,
            targetHeight = 150,
            rotationDegrees = 90,
            fitMode = FitMode.FIT,
            adjustments = ImageAdjustments(brightness = 10, contrast = 10),
            filter = FilterType.WARM,
            outputFormat = OutputFormat.JPG,
            quality = 80
        )

        val transformed = ImageEngine.applyTransformations(source, state)
        assertNotNull(transformed)
        assertEquals(150, transformed.width)
        assertEquals(150, transformed.height)

        val bytes = ImageEngine.compressBitmap(transformed, OutputFormat.JPG, 80)
        assertTrue(bytes.isNotEmpty())
    }
}
