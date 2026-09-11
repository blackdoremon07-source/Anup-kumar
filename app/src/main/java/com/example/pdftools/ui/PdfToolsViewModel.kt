package com.example.pdftools.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdftools.engine.PdfEngine
import com.example.pdftools.model.PdfCompressionLevel
import com.example.pdftools.model.PdfCompressionResult
import com.example.pdftools.model.PdfFitMode
import com.example.pdftools.model.PdfImageItem
import com.example.pdftools.model.PdfPageMargin
import com.example.pdftools.model.PdfPageOrientation
import com.example.pdftools.model.PdfPageSize
import com.example.pdftools.model.PdfPageThumbnail
import com.example.pdftools.model.PdfSourceFile
import com.example.pdftools.model.PdfToolType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PdfUiState(
    val activeTool: PdfToolType = PdfToolType.IMAGE_TO_PDF,
    val statusMessage: String? = null,
    val isProcessing: Boolean = false,

    // Image to PDF
    val imagesList: List<PdfImageItem> = emptyList(),
    val pageSize: PdfPageSize = PdfPageSize.A4,
    val pageOrientation: PdfPageOrientation = PdfPageOrientation.PORTRAIT,
    val pageMargin: PdfPageMargin = PdfPageMargin.SMALL,
    val fitMode: PdfFitMode = PdfFitMode.FIT,
    val generatedImagePdfBytes: ByteArray? = null,

    // PDF Compressor
    val compressorSource: PdfSourceFile? = null,
    val compressionLevel: PdfCompressionLevel = PdfCompressionLevel.MEDIUM,
    val compressionResult: PdfCompressionResult? = null,

    // Merge PDF
    val mergeFilesList: List<PdfSourceFile> = emptyList(),
    val mergedPdfBytes: ByteArray? = null,

    // Split PDF
    val splitSource: PdfSourceFile? = null,
    val splitThumbnails: List<PdfPageThumbnail> = emptyList(),
    val splitSelectedPages: Set<Int> = emptySet(),
    val splitResultBytes: ByteArray? = null,

    // Reorder PDF
    val reorderSource: PdfSourceFile? = null,
    val reorderPages: List<PdfPageThumbnail> = emptyList(),
    val reorderedResultBytes: ByteArray? = null,

    // PDF to Images
    val pdfToImgSource: PdfSourceFile? = null,
    val convertedImages: List<Bitmap> = emptyList(),
    val imageFormat: String = "JPEG", // JPEG or PNG

    // PDF Preview
    val previewSource: PdfSourceFile? = null,
    val previewPages: List<Bitmap> = emptyList(),
    val previewCurrentPageIndex: Int = 0,
    val previewZoomScale: Float = 1.0f
)

class PdfToolsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PdfUiState())
    val uiState: StateFlow<PdfUiState> = _uiState.asStateFlow()

    fun selectTool(tool: PdfToolType) {
        _uiState.update { it.copy(activeTool = tool, statusMessage = null) }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    // ==========================================
    // A. IMAGE TO PDF
    // ==========================================

    fun addImages(uris: List<Uri>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val newItems = mutableListOf<PdfImageItem>()
            withContext(Dispatchers.IO) {
                val cr = getApplication<Application>().contentResolver
                uris.forEachIndexed { index, uri ->
                    try {
                        cr.openInputStream(uri)?.use { stream ->
                            val bmp = BitmapFactory.decodeStream(stream)
                            if (bmp != null) {
                                newItems.add(
                                    PdfImageItem(
                                        uri = uri,
                                        bitmap = bmp,
                                        fileName = "Photo_${index + 1}"
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            _uiState.update {
                it.copy(
                    imagesList = it.imagesList + newItems,
                    isProcessing = false,
                    statusMessage = if (newItems.isNotEmpty()) "Added ${newItems.size} image(s)" else null,
                    generatedImagePdfBytes = null
                )
            }
        }
    }

    fun removeImage(index: Int) {
        _uiState.update { state ->
            val list = state.imagesList.toMutableList()
            if (index in list.indices) {
                val removed = list.removeAt(index)
                if (!removed.bitmap.isRecycled) removed.bitmap.recycle()
            }
            state.copy(imagesList = list, generatedImagePdfBytes = null)
        }
    }

    fun moveImage(fromIndex: Int, toIndex: Int) {
        _uiState.update { state ->
            val list = state.imagesList.toMutableList()
            if (fromIndex in list.indices && toIndex in list.indices) {
                val item = list.removeAt(fromIndex)
                list.add(toIndex, item)
            }
            state.copy(imagesList = list, generatedImagePdfBytes = null)
        }
    }

    fun rotateImage(index: Int) {
        _uiState.update { state ->
            val list = state.imagesList.toMutableList()
            if (index in list.indices) {
                val item = list[index]
                list[index] = item.copy(rotationDegrees = (item.rotationDegrees + 90) % 360)
            }
            state.copy(imagesList = list, generatedImagePdfBytes = null)
        }
    }

    fun setPageSize(size: PdfPageSize) {
        _uiState.update { it.copy(pageSize = size, generatedImagePdfBytes = null) }
    }

    fun setPageOrientation(orientation: PdfPageOrientation) {
        _uiState.update { it.copy(pageOrientation = orientation, generatedImagePdfBytes = null) }
    }

    fun setPageMargin(margin: PdfPageMargin) {
        _uiState.update { it.copy(pageMargin = margin, generatedImagePdfBytes = null) }
    }

    fun setFitMode(mode: PdfFitMode) {
        _uiState.update { it.copy(fitMode = mode, generatedImagePdfBytes = null) }
    }

    fun generateImagePdf() {
        val state = _uiState.value
        if (state.imagesList.isEmpty()) {
            _uiState.update { it.copy(statusMessage = "Please add at least one image") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, statusMessage = "Generating PDF document...") }
            val bytes = PdfEngine.createPdfFromImages(
                images = state.imagesList,
                pageSize = state.pageSize,
                orientation = state.pageOrientation,
                margin = state.pageMargin,
                fitMode = state.fitMode
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    generatedImagePdfBytes = bytes,
                    statusMessage = "PDF generated successfully (${bytes.size / 1024} KB)!"
                )
            }
        }
    }

    fun downloadImagePdf() {
        val bytes = _uiState.value.generatedImagePdfBytes ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val result = PdfEngine.saveFileToStorage(
                context = getApplication(),
                bytes = bytes,
                fileName = "DG_Images_${System.currentTimeMillis()}.pdf",
                mimeType = "application/pdf"
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    statusMessage = result.getOrElse { e -> "Download failed: ${e.localizedMessage}" }
                )
            }
        }
    }

    // ==========================================
    // B. PDF COMPRESSOR
    // ==========================================

    fun selectCompressorPdf(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val file = loadPdfSourceFile(uri)
            _uiState.update {
                it.copy(
                    compressorSource = file,
                    compressionResult = null,
                    isProcessing = false,
                    statusMessage = if (file != null) "Loaded ${file.fileName} (${file.fileSizeBytes / 1024} KB, ${file.pageCount} pages)" else "Failed to load PDF"
                )
            }
        }
    }

    fun setCompressionLevel(level: PdfCompressionLevel) {
        _uiState.update { it.copy(compressionLevel = level, compressionResult = null) }
    }

    fun compressPdf() {
        val src = _uiState.value.compressorSource ?: return
        val level = _uiState.value.compressionLevel

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, statusMessage = "Compressing PDF...") }
            val result = PdfEngine.compressPdf(
                context = getApplication(),
                pdfBytes = src.fileBytes,
                level = level
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    compressionResult = result,
                    statusMessage = result.explanationNotice
                )
            }
        }
    }

    fun downloadCompressedPdf() {
        val bytes = _uiState.value.compressionResult?.outputBytes ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val result = PdfEngine.saveFileToStorage(
                context = getApplication(),
                bytes = bytes,
                fileName = "DG_Compressed_${System.currentTimeMillis()}.pdf",
                mimeType = "application/pdf"
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    statusMessage = result.getOrElse { e -> "Download failed: ${e.localizedMessage}" }
                )
            }
        }
    }

    // ==========================================
    // C. MERGE PDF
    // ==========================================

    fun addMergePdfs(uris: List<Uri>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val loaded = mutableListOf<PdfSourceFile>()
            uris.forEach { uri ->
                loadPdfSourceFile(uri)?.let { loaded.add(it) }
            }
            _uiState.update {
                it.copy(
                    mergeFilesList = it.mergeFilesList + loaded,
                    mergedPdfBytes = null,
                    isProcessing = false,
                    statusMessage = "Added ${loaded.size} PDF document(s) for merging"
                )
            }
        }
    }

    fun removeMergeFile(index: Int) {
        _uiState.update { state ->
            val list = state.mergeFilesList.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(mergeFilesList = list, mergedPdfBytes = null)
        }
    }

    fun moveMergeFile(fromIndex: Int, toIndex: Int) {
        _uiState.update { state ->
            val list = state.mergeFilesList.toMutableList()
            if (fromIndex in list.indices && toIndex in list.indices) {
                val item = list.removeAt(fromIndex)
                list.add(toIndex, item)
            }
            state.copy(mergeFilesList = list, mergedPdfBytes = null)
        }
    }

    fun mergePdfs() {
        val files = _uiState.value.mergeFilesList
        if (files.size < 2) {
            _uiState.update { it.copy(statusMessage = "Please add at least 2 PDF files to merge") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, statusMessage = "Merging PDF files...") }
            val bytes = PdfEngine.mergePdfs(
                context = getApplication(),
                pdfList = files.map { it.fileBytes }
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    mergedPdfBytes = bytes,
                    statusMessage = "Successfully merged into single PDF (${bytes.size / 1024} KB)!"
                )
            }
        }
    }

    fun downloadMergedPdf() {
        val bytes = _uiState.value.mergedPdfBytes ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val result = PdfEngine.saveFileToStorage(
                context = getApplication(),
                bytes = bytes,
                fileName = "DG_Merged_${System.currentTimeMillis()}.pdf",
                mimeType = "application/pdf"
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    statusMessage = result.getOrElse { e -> "Download failed: ${e.localizedMessage}" }
                )
            }
        }
    }

    // ==========================================
    // D. SPLIT PDF
    // ==========================================

    fun selectSplitPdf(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val file = loadPdfSourceFile(uri)
            if (file != null) {
                val rendered = PdfEngine.renderPdfPages(getApplication(), file.fileBytes, maxPages = 50, renderScale = 0.5f)
                val thumbnails = rendered.mapIndexed { idx, bmp ->
                    PdfPageThumbnail(pageIndex = idx, thumbnail = bmp, isSelected = true)
                }
                _uiState.update {
                    it.copy(
                        splitSource = file,
                        splitThumbnails = thumbnails,
                        splitSelectedPages = thumbnails.indices.toSet(),
                        splitResultBytes = null,
                        isProcessing = false,
                        statusMessage = "Loaded ${file.fileName} (${thumbnails.size} pages)"
                    )
                }
            } else {
                _uiState.update { it.copy(isProcessing = false, statusMessage = "Failed to load PDF") }
            }
        }
    }

    fun toggleSplitPage(pageIndex: Int) {
        _uiState.update { state ->
            val set = state.splitSelectedPages.toMutableSet()
            if (pageIndex in set) set.remove(pageIndex) else set.add(pageIndex)
            state.copy(splitSelectedPages = set, splitResultBytes = null)
        }
    }

    fun selectAllSplitPages(select: Boolean) {
        _uiState.update { state ->
            val set = if (select) state.splitThumbnails.indices.toSet() else emptySet()
            state.copy(splitSelectedPages = set, splitResultBytes = null)
        }
    }

    fun extractSplitPdf() {
        val src = _uiState.value.splitSource ?: return
        val selected = _uiState.value.splitSelectedPages
        if (selected.isEmpty()) {
            _uiState.update { it.copy(statusMessage = "Please select at least one page to extract") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, statusMessage = "Extracting selected pages...") }
            val bytes = PdfEngine.splitPdf(
                context = getApplication(),
                pdfBytes = src.fileBytes,
                selectedPageIndices = selected
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    splitResultBytes = bytes,
                    statusMessage = "Extracted ${selected.size} page(s) (${bytes.size / 1024} KB)!"
                )
            }
        }
    }

    fun downloadSplitPdf() {
        val bytes = _uiState.value.splitResultBytes ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val result = PdfEngine.saveFileToStorage(
                context = getApplication(),
                bytes = bytes,
                fileName = "DG_Split_${System.currentTimeMillis()}.pdf",
                mimeType = "application/pdf"
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    statusMessage = result.getOrElse { e -> "Download failed: ${e.localizedMessage}" }
                )
            }
        }
    }

    // ==========================================
    // E. PDF PAGE REORDER
    // ==========================================

    fun selectReorderPdf(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val file = loadPdfSourceFile(uri)
            if (file != null) {
                val rendered = PdfEngine.renderPdfPages(getApplication(), file.fileBytes, maxPages = 50, renderScale = 0.5f)
                val thumbs = rendered.mapIndexed { idx, bmp ->
                    PdfPageThumbnail(pageIndex = idx, thumbnail = bmp, isSelected = true)
                }
                _uiState.update {
                    it.copy(
                        reorderSource = file,
                        reorderPages = thumbs,
                        reorderedResultBytes = null,
                        isProcessing = false,
                        statusMessage = "Loaded ${file.fileName} with ${thumbs.size} pages"
                    )
                }
            } else {
                _uiState.update { it.copy(isProcessing = false, statusMessage = "Failed to load PDF") }
            }
        }
    }

    fun moveReorderPage(fromIndex: Int, toIndex: Int) {
        _uiState.update { state ->
            val list = state.reorderPages.toMutableList()
            if (fromIndex in list.indices && toIndex in list.indices) {
                val item = list.removeAt(fromIndex)
                list.add(toIndex, item)
            }
            state.copy(reorderPages = list, reorderedResultBytes = null)
        }
    }

    fun saveReorderedPdf() {
        val src = _uiState.value.reorderSource ?: return
        val orderedIndices = _uiState.value.reorderPages.map { it.pageIndex }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, statusMessage = "Reordering PDF pages...") }
            val bytes = PdfEngine.reorderPdf(
                context = getApplication(),
                pdfBytes = src.fileBytes,
                orderedPageIndices = orderedIndices
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    reorderedResultBytes = bytes,
                    statusMessage = "Pages reordered successfully (${bytes.size / 1024} KB)!"
                )
            }
        }
    }

    fun downloadReorderedPdf() {
        val bytes = _uiState.value.reorderedResultBytes ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val result = PdfEngine.saveFileToStorage(
                context = getApplication(),
                bytes = bytes,
                fileName = "DG_Reordered_${System.currentTimeMillis()}.pdf",
                mimeType = "application/pdf"
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    statusMessage = result.getOrElse { e -> "Download failed: ${e.localizedMessage}" }
                )
            }
        }
    }

    // ==========================================
    // F. PDF TO IMAGES
    // ==========================================

    fun selectPdfToImages(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val file = loadPdfSourceFile(uri)
            if (file != null) {
                val rendered = PdfEngine.renderPdfPages(getApplication(), file.fileBytes, maxPages = 30, renderScale = 1.5f)
                _uiState.update {
                    it.copy(
                        pdfToImgSource = file,
                        convertedImages = rendered,
                        isProcessing = false,
                        statusMessage = "Converted ${rendered.size} pages from ${file.fileName}"
                    )
                }
            } else {
                _uiState.update { it.copy(isProcessing = false, statusMessage = "Failed to load PDF") }
            }
        }
    }

    fun setImageFormat(format: String) {
        _uiState.update { it.copy(imageFormat = format) }
    }

    fun downloadAllImagesZip() {
        val src = _uiState.value.pdfToImgSource ?: return
        val format = if (_uiState.value.imageFormat == "PNG") Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
        val ext = if (_uiState.value.imageFormat == "PNG") "png" else "jpg"

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, statusMessage = "Packaging images into ZIP...") }
            val zipBytes = PdfEngine.convertPdfToImagesZip(
                context = getApplication(),
                pdfBytes = src.fileBytes,
                format = format,
                fileExtension = ext
            )
            val result = PdfEngine.saveFileToStorage(
                context = getApplication(),
                bytes = zipBytes,
                fileName = "DG_PDF_Images_${System.currentTimeMillis()}.zip",
                mimeType = "application/zip"
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    statusMessage = result.getOrElse { e -> "Download failed: ${e.localizedMessage}" }
                )
            }
        }
    }

    // ==========================================
    // G. PDF PREVIEW / VIEWER
    // ==========================================

    fun selectPreviewPdf(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val file = loadPdfSourceFile(uri)
            if (file != null) {
                val pages = PdfEngine.renderPdfPages(getApplication(), file.fileBytes, maxPages = 50, renderScale = 1.0f)
                _uiState.update {
                    it.copy(
                        previewSource = file,
                        previewPages = pages,
                        previewCurrentPageIndex = 0,
                        previewZoomScale = 1.0f,
                        isProcessing = false,
                        statusMessage = "Ready: ${file.fileName} (${pages.size} pages, ${file.fileSizeBytes / 1024} KB)"
                    )
                }
            } else {
                _uiState.update { it.copy(isProcessing = false, statusMessage = "Failed to open PDF") }
            }
        }
    }

    fun setPreviewPageIndex(index: Int) {
        _uiState.update {
            if (index in it.previewPages.indices) it.copy(previewCurrentPageIndex = index) else it
        }
    }

    fun setPreviewZoom(zoom: Float) {
        _uiState.update { it.copy(previewZoomScale = zoom.coerceIn(0.5f, 3.0f)) }
    }

    // ==========================================
    // INTERNAL HELPER
    // ==========================================

    private suspend fun loadPdfSourceFile(uri: Uri): PdfSourceFile? = withContext(Dispatchers.IO) {
        try {
            val cr = getApplication<Application>().contentResolver
            val bytes = cr.openInputStream(uri)?.use { it.readBytes() } ?: return@withContext null
            val pageCount = PdfEngine.getPdfPageCount(getApplication(), bytes)
            val thumbs = PdfEngine.renderPdfPages(getApplication(), bytes, maxPages = 1, renderScale = 0.3f)
            val name = uri.lastPathSegment?.substringAfterLast('/') ?: "document.pdf"

            PdfSourceFile(
                uri = uri,
                fileName = if (name.endsWith(".pdf", ignoreCase = true)) name else "$name.pdf",
                fileSizeBytes = bytes.size.toLong(),
                pageCount = pageCount,
                thumbnail = thumbs.firstOrNull(),
                fileBytes = bytes
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
