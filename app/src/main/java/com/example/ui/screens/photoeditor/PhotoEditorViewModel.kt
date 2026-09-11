package com.example.ui.screens.photoeditor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.editor.engine.ImageEngine
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
import com.example.editor.validation.ValidationEngine
import com.example.editor.model.ValidationReport
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

sealed class EditorUiEvent {
    data class ShowMessage(val message: String) : EditorUiEvent()
    data class DownloadSuccess(val uri: Uri, val path: String) : EditorUiEvent()
}

class PhotoEditorViewModel : ViewModel() {

    private val _originalBitmap = MutableStateFlow<Bitmap?>(null)
    val originalBitmap: StateFlow<Bitmap?> = _originalBitmap.asStateFlow()

    private val _previewBitmap = MutableStateFlow<Bitmap?>(null)
    val previewBitmap: StateFlow<Bitmap?> = _previewBitmap.asStateFlow()

    private val _originalMetadata = MutableStateFlow<ImageMetadata?>(null)
    val originalMetadata: StateFlow<ImageMetadata?> = _originalMetadata.asStateFlow()

    private val _currentState = MutableStateFlow(EditorState())
    val currentState: StateFlow<EditorState> = _currentState.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _estimatedSizeBytes = MutableStateFlow(0L)
    val estimatedSizeBytes: StateFlow<Long> = _estimatedSizeBytes.asStateFlow()

    private val _targetSizeResult = MutableStateFlow<TargetSizeResult?>(null)
    val targetSizeResult: StateFlow<TargetSizeResult?> = _targetSizeResult.asStateFlow()

    private val _validationReport = MutableStateFlow<ValidationReport?>(null)
    val validationReport: StateFlow<ValidationReport?> = _validationReport.asStateFlow()

    private val _eventFlow = MutableSharedFlow<EditorUiEvent>()
    val eventFlow: SharedFlow<EditorUiEvent> = _eventFlow.asSharedFlow()

    // Undo / Redo stacks
    private val undoStack = ArrayDeque<EditorState>()
    private val redoStack = ArrayDeque<EditorState>()

    private var renderJob: Job? = null

    init {
        // Initialize with default sample candidate photo so the screen is immediately interactive!
        loadSamplePhoto()
    }

    fun loadSamplePhoto() {
        viewModelScope.launch {
            _isProcessing.value = true
            val (bitmap, metadata) = ImageEngine.generateSampleCandidatePhoto()
            setNewImage(bitmap, metadata)
            _isProcessing.value = false
        }
    }

    fun loadImageFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            val result = ImageEngine.loadBitmapFromUri(context, uri)
            if (result != null) {
                setNewImage(result.first, result.second)
                _eventFlow.emit(EditorUiEvent.ShowMessage("Photo loaded successfully / फोटो सफलतापूर्वक लोड की गई"))
            } else {
                _eventFlow.emit(
                    EditorUiEvent.ShowMessage(
                        "Please select a valid JPG, JPEG, PNG or WebP image. / कृपया मान्य फोटो चुनें।"
                    )
                )
            }
            _isProcessing.value = false
        }
    }

    private fun setNewImage(bitmap: Bitmap, metadata: ImageMetadata) {
        _originalBitmap.value = bitmap
        _originalMetadata.value = metadata
        undoStack.clear()
        redoStack.clear()

        val initialState = EditorState(
            originalWidth = bitmap.width,
            originalHeight = bitmap.height,
            targetWidth = bitmap.width,
            targetHeight = bitmap.height,
            isAspectLocked = true,
            selectedAspectRatioPreset = AspectRatioPreset.ORIGINAL,
            cropRect = NormalizedCropRect.FULL,
            outputFormat = OutputFormat.JPG,
            quality = 90
        )
        _currentState.value = initialState
        triggerRender(initialState, debounceMs = 0)
    }

    fun removePhoto() {
        _originalBitmap.value = null
        _previewBitmap.value = null
        _originalMetadata.value = null
        undoStack.clear()
        redoStack.clear()
        _currentState.value = EditorState()
        _validationReport.value = null
        _targetSizeResult.value = null
    }

    fun canUndo(): Boolean = undoStack.isNotEmpty()
    fun canRedo(): Boolean = redoStack.isNotEmpty()

    fun undo() {
        if (undoStack.isEmpty()) return
        val previousState = undoStack.removeLast()
        redoStack.addLast(_currentState.value)
        _currentState.value = previousState
        triggerRender(previousState, debounceMs = 0)
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val nextState = redoStack.removeLast()
        undoStack.addLast(_currentState.value)
        _currentState.value = nextState
        triggerRender(nextState, debounceMs = 0)
    }

    private fun pushUndoState() {
        if (undoStack.size >= 25) {
            undoStack.removeFirst()
        }
        undoStack.addLast(_currentState.value)
        redoStack.clear()
    }

    fun updateDimensions(width: Int, height: Int, lockAspect: Boolean? = null) {
        val current = _currentState.value
        pushUndoState()

        val isLocked = lockAspect ?: current.isAspectLocked
        var newW = width.coerceIn(1, 8000)
        var newH = height.coerceIn(1, 8000)

        val updated = current.copy(
            targetWidth = newW,
            targetHeight = newH,
            isAspectLocked = isLocked,
            resizePercentage = ((newW.toFloat() / current.originalWidth) * 100).roundToInt().coerceIn(1, 200)
        )
        _currentState.value = updated
        triggerRender(updated)
    }

    fun setLockAspectRatio(locked: Boolean) {
        pushUndoState()
        _currentState.value = _currentState.value.copy(isAspectLocked = locked)
    }

    fun updateWidthLocked(newWidth: Int) {
        val current = _currentState.value
        if (newWidth <= 0) return
        pushUndoState()

        val ratio = if (current.targetHeight > 0) {
            current.targetWidth.toFloat() / current.targetHeight.toFloat()
        } else {
            current.originalWidth.toFloat() / current.originalHeight.toFloat()
        }

        val newHeight = if (current.isAspectLocked && ratio > 0) {
            (newWidth / ratio).roundToInt().coerceAtLeast(1)
        } else {
            current.targetHeight
        }

        val updated = current.copy(
            targetWidth = newWidth,
            targetHeight = newHeight,
            resizePercentage = ((newWidth.toFloat() / current.originalWidth) * 100).roundToInt().coerceIn(1, 200)
        )
        _currentState.value = updated
        triggerRender(updated)
    }

    fun updateHeightLocked(newHeight: Int) {
        val current = _currentState.value
        if (newHeight <= 0) return
        pushUndoState()

        val ratio = if (current.targetHeight > 0) {
            current.targetWidth.toFloat() / current.targetHeight.toFloat()
        } else {
            current.originalWidth.toFloat() / current.originalHeight.toFloat()
        }

        val newWidth = if (current.isAspectLocked && ratio > 0) {
            (newHeight * ratio).roundToInt().coerceAtLeast(1)
        } else {
            current.targetWidth
        }

        val updated = current.copy(
            targetWidth = newWidth,
            targetHeight = newHeight,
            resizePercentage = ((newWidth.toFloat() / current.originalWidth) * 100).roundToInt().coerceIn(1, 200)
        )
        _currentState.value = updated
        triggerRender(updated)
    }

    fun setResizePercentage(percentage: Int) {
        val current = _currentState.value
        val clamped = percentage.coerceIn(1, 100)
        pushUndoState()

        val newW = ((current.originalWidth * clamped) / 100).coerceAtLeast(1)
        val newH = ((current.originalHeight * clamped) / 100).coerceAtLeast(1)

        val updated = current.copy(
            targetWidth = newW,
            targetHeight = newH,
            resizePercentage = clamped
        )
        _currentState.value = updated
        triggerRender(updated)
    }

    fun setAspectRatioPreset(preset: AspectRatioPreset) {
        val current = _currentState.value
        pushUndoState()

        val origW = current.originalWidth
        val origH = current.originalHeight
        if (origW <= 0 || origH <= 0) return

        val newCrop = if (preset == AspectRatioPreset.ORIGINAL) {
            NormalizedCropRect.FULL
        } else if (preset == AspectRatioPreset.CUSTOM) {
            current.cropRect
        } else {
            val targetRatio = preset.calculateAspectRatio(origW, origH)
            val currentImgRatio = origW.toFloat() / origH.toFloat()

            if (targetRatio > currentImgRatio) {
                // Wider than image: full width, reduced height centered
                val normH = (currentImgRatio / targetRatio).coerceIn(0.1f, 1f)
                val top = (1f - normH) / 2f
                NormalizedCropRect(0f, top, 1f, top + normH)
            } else {
                // Taller than image: full height, reduced width centered
                val normW = (targetRatio / currentImgRatio).coerceIn(0.1f, 1f)
                val left = (1f - normW) / 2f
                NormalizedCropRect(left, 0f, left + normW, 1f)
            }
        }

        val updated = current.copy(
            selectedAspectRatioPreset = preset,
            cropRect = newCrop
        )
        _currentState.value = updated
        triggerRender(updated)
    }

    fun updateCropRect(cropRect: NormalizedCropRect) {
        pushUndoState()
        val updated = _currentState.value.copy(
            cropRect = cropRect,
            selectedAspectRatioPreset = AspectRatioPreset.CUSTOM
        )
        _currentState.value = updated
        triggerRender(updated)
    }

    fun resetCrop() {
        pushUndoState()
        val updated = _currentState.value.copy(
            cropRect = NormalizedCropRect.FULL,
            selectedAspectRatioPreset = AspectRatioPreset.ORIGINAL
        )
        _currentState.value = updated
        triggerRender(updated)
    }

    fun setFitMode(fitMode: FitMode) {
        pushUndoState()
        val updated = _currentState.value.copy(fitMode = fitMode)
        _currentState.value = updated
        triggerRender(updated)
    }

    fun rotateLeft() {
        pushUndoState()
        val cur = _currentState.value
        val newRotation = (cur.rotationDegrees - 90 + 360) % 360
        // Swap target width and height if rotated 90 or 270 degrees
        val updated = cur.copy(
            rotationDegrees = newRotation,
            targetWidth = cur.targetHeight,
            targetHeight = cur.targetWidth
        )
        _currentState.value = updated
        triggerRender(updated)
    }

    fun rotateRight() {
        pushUndoState()
        val cur = _currentState.value
        val newRotation = (cur.rotationDegrees + 90) % 360
        val updated = cur.copy(
            rotationDegrees = newRotation,
            targetWidth = cur.targetHeight,
            targetHeight = cur.targetWidth
        )
        _currentState.value = updated
        triggerRender(updated)
    }

    fun flipHorizontal() {
        pushUndoState()
        val cur = _currentState.value
        val updated = cur.copy(isFlippedHorizontal = !cur.isFlippedHorizontal)
        _currentState.value = updated
        triggerRender(updated)
    }

    fun flipVertical() {
        pushUndoState()
        val cur = _currentState.value
        val updated = cur.copy(isFlippedVertical = !cur.isFlippedVertical)
        _currentState.value = updated
        triggerRender(updated)
    }

    fun updateAdjustments(adjustments: ImageAdjustments) {
        pushUndoState()
        val updated = _currentState.value.copy(adjustments = adjustments)
        _currentState.value = updated
        triggerRender(updated, debounceMs = 120)
    }

    fun resetAdjustments() {
        pushUndoState()
        val updated = _currentState.value.copy(adjustments = ImageAdjustments())
        _currentState.value = updated
        triggerRender(updated)
    }

    fun setFilter(filter: FilterType) {
        pushUndoState()
        val updated = _currentState.value.copy(filter = filter)
        _currentState.value = updated
        triggerRender(updated)
    }

    fun setBackgroundColor(bg: BackgroundColorOption) {
        pushUndoState()
        val updated = _currentState.value.copy(backgroundColor = bg)
        _currentState.value = updated
        triggerRender(updated)
    }

    fun setOutputFormat(format: OutputFormat) {
        pushUndoState()
        val updated = _currentState.value.copy(outputFormat = format)
        _currentState.value = updated
        triggerRender(updated)
    }

    fun setQuality(quality: Int) {
        val clamped = quality.coerceIn(1, 100)
        val updated = _currentState.value.copy(quality = clamped)
        _currentState.value = updated
        triggerRender(updated, debounceMs = 100)
    }

    fun setTargetSizeEnabled(enabled: Boolean) {
        pushUndoState()
        val updated = _currentState.value.copy(isTargetSizeEnabled = enabled)
        _currentState.value = updated
        triggerRender(updated)
    }

    fun setTargetSizeKb(targetKb: Int) {
        val clamped = targetKb.coerceIn(5, 5000)
        pushUndoState()
        val updated = _currentState.value.copy(targetSizeKb = clamped)
        _currentState.value = updated
        triggerRender(updated)
    }

    fun resetEverything() {
        val original = _originalBitmap.value ?: return
        undoStack.clear()
        redoStack.clear()
        val cleanState = EditorState(
            originalWidth = original.width,
            originalHeight = original.height,
            targetWidth = original.width,
            targetHeight = original.height,
            isAspectLocked = true,
            selectedAspectRatioPreset = AspectRatioPreset.ORIGINAL,
            cropRect = NormalizedCropRect.FULL,
            outputFormat = OutputFormat.JPG,
            quality = 90
        )
        _currentState.value = cleanState
        triggerRender(cleanState, debounceMs = 0)
        viewModelScope.launch {
            _eventFlow.emit(EditorUiEvent.ShowMessage("Editor reset to original / सभी बदलाव रीसेट कर दिए गए"))
        }
    }

    private fun triggerRender(state: EditorState, debounceMs: Long = 60) {
        renderJob?.cancel()
        renderJob = viewModelScope.launch {
            if (debounceMs > 0) delay(debounceMs)
            val source = _originalBitmap.value ?: return@launch
            _isProcessing.value = true

            // Apply transforms and color pipeline
            val rendered = ImageEngine.applyTransformations(source, state)
            _previewBitmap.value = rendered

            // Target size optimization check if enabled
            if (state.isTargetSizeEnabled) {
                val optResult = ImageEngine.optimizeForTargetSize(
                    rendered,
                    state.outputFormat,
                    state.targetSizeKb
                )
                _targetSizeResult.value = optResult
                _estimatedSizeBytes.value = optResult.actualBytes
            } else {
                _targetSizeResult.value = null
                val bytes = ImageEngine.compressBitmap(rendered, state.outputFormat, state.quality)
                _estimatedSizeBytes.value = bytes.size.toLong()
            }

            // Live validation update
            val report = com.example.editor.validation.ValidationEngine.validate(
                state = state,
                actualFileSizeBytes = _estimatedSizeBytes.value
            )
            _validationReport.value = report

            _isProcessing.value = false
        }
    }

    fun downloadPhoto(context: Context) {
        viewModelScope.launch {
            val bitmap = _previewBitmap.value ?: return@launch
            val state = _currentState.value
            _isProcessing.value = true

            val qualityToUse = if (state.isTargetSizeEnabled) {
                _targetSizeResult.value?.qualityUsed ?: state.quality
            } else {
                state.quality
            }

            val compressedBytes = ImageEngine.compressBitmap(bitmap, state.outputFormat, qualityToUse)
            val savedUri = ImageEngine.savePhotoToDevice(
                context = context,
                bytes = compressedBytes,
                format = state.outputFormat,
                suggestedName = "DG_with_Anup_Edited_Photo"
            )

            _isProcessing.value = false
            if (savedUri != null) {
                _eventFlow.emit(
                    EditorUiEvent.DownloadSuccess(
                        uri = savedUri,
                        path = "Saved to Pictures/DG_with_Anup / फोटो सेव हो गई"
                    )
                )
            } else {
                _eventFlow.emit(
                    EditorUiEvent.ShowMessage("Failed to save photo / फोटो सेव करने में त्रुटि हुई")
                )
            }
        }
    }
}
