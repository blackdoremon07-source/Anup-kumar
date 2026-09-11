package com.example.pdftools.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pdftools.model.PdfCompressionLevel
import com.example.pdftools.model.PdfFitMode
import com.example.pdftools.model.PdfImageItem
import com.example.pdftools.model.PdfPageMargin
import com.example.pdftools.model.PdfPageOrientation
import com.example.pdftools.model.PdfPageSize
import com.example.pdftools.model.PdfToolType
import com.example.ui.components.DgOutlinedButton
import com.example.ui.components.DgPrimaryButton
import com.example.ui.theme.DgAmberLight
import com.example.ui.theme.DgBackgroundLight
import com.example.ui.theme.DgBlueLight
import com.example.ui.theme.DgBorderLight
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfToolsScreen(
    onBack: () -> Unit,
    viewModel: PdfToolsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Pickers
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 25)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.addImages(uris)
        }
    }

    val pdfCompressorPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.selectCompressorPdf(it) }
    }

    val pdfMergePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.addMergePdfs(uris)
        }
    }

    val pdfSplitPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.selectSplitPdf(it) }
    }

    val pdfReorderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.selectReorderPdf(it) }
    }

    val pdfToImagesPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.selectPdfToImages(it) }
    }

    val pdfPreviewPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.selectPreviewPdf(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DgBackgroundLight)
            .statusBarsPadding()
            .testTag("screen_pdf_tools")
    ) {
        // App Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("pdf_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DgNavyPrimary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PDF Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFDC2626))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "OFFLINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Text(
                        text = "DG with Anup • 100% Client-Side Private",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Private",
                    tint = DgEmerald,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Sub-tool Tab Bar
        ScrollableToolTabs(
            selectedTool = state.activeTool,
            onSelectTool = { viewModel.selectTool(it) }
        )

        // Status banner
        AnimatedVisibility(visible = state.statusMessage != null) {
            state.statusMessage?.let { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = DgBlueLight),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBFDBFE)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = DgNavyPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = msg,
                            fontSize = 12.sp,
                            color = DgNavyDark,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearStatusMessage() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Dismiss",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        if (state.isProcessing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = DgNavyPrimary,
                trackColor = Color(0xFFE2E8F0)
            )
        }

        // Tool Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (state.activeTool) {
                PdfToolType.IMAGE_TO_PDF -> ImageToPdfContent(
                    state = state,
                    onPickImages = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onRemoveImage = { viewModel.removeImage(it) },
                    onMoveImage = { from, to -> viewModel.moveImage(from, to) },
                    onRotateImage = { viewModel.rotateImage(it) },
                    onSetPageSize = { viewModel.setPageSize(it) },
                    onSetOrientation = { viewModel.setPageOrientation(it) },
                    onSetMargin = { viewModel.setPageMargin(it) },
                    onSetFitMode = { viewModel.setFitMode(it) },
                    onGenerate = { viewModel.generateImagePdf() },
                    onDownload = { viewModel.downloadImagePdf() }
                )

                PdfToolType.COMPRESSOR -> CompressorContent(
                    state = state,
                    onPickPdf = { pdfCompressorPicker.launch("application/pdf") },
                    onSetLevel = { viewModel.setCompressionLevel(it) },
                    onCompress = { viewModel.compressPdf() },
                    onDownload = { viewModel.downloadCompressedPdf() }
                )

                PdfToolType.MERGE -> MergePdfContent(
                    state = state,
                    onPickPdfs = { pdfMergePicker.launch("application/pdf") },
                    onRemove = { viewModel.removeMergeFile(it) },
                    onMove = { from, to -> viewModel.moveMergeFile(from, to) },
                    onMerge = { viewModel.mergePdfs() },
                    onDownload = { viewModel.downloadMergedPdf() }
                )

                PdfToolType.SPLIT -> SplitPdfContent(
                    state = state,
                    onPickPdf = { pdfSplitPicker.launch("application/pdf") },
                    onTogglePage = { viewModel.toggleSplitPage(it) },
                    onSelectAll = { viewModel.selectAllSplitPages(it) },
                    onExtract = { viewModel.extractSplitPdf() },
                    onDownload = { viewModel.downloadSplitPdf() }
                )

                PdfToolType.REORDER -> ReorderPdfContent(
                    state = state,
                    onPickPdf = { pdfReorderPicker.launch("application/pdf") },
                    onMovePage = { from, to -> viewModel.moveReorderPage(from, to) },
                    onSave = { viewModel.saveReorderedPdf() },
                    onDownload = { viewModel.downloadReorderedPdf() }
                )

                PdfToolType.PDF_TO_IMAGES -> PdfToImagesContent(
                    state = state,
                    onPickPdf = { pdfToImagesPicker.launch("application/pdf") },
                    onSetFormat = { viewModel.setImageFormat(it) },
                    onDownloadAll = { viewModel.downloadAllImagesZip() }
                )

                PdfToolType.VIEWER -> PdfViewerContent(
                    state = state,
                    onPickPdf = { pdfPreviewPicker.launch("application/pdf") },
                    onSetPageIndex = { viewModel.setPreviewPageIndex(it) },
                    onSetZoom = { viewModel.setPreviewZoom(it) }
                )
            }
        }
    }
}

@Composable
private fun ScrollableToolTabs(
    selectedTool: PdfToolType,
    onSelectTool: (PdfToolType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PdfToolType.values().forEach { tool ->
            val isSelected = selectedTool == tool
            FilterChip(
                selected = isSelected,
                onClick = { onSelectTool(tool) },
                label = {
                    Text(
                        text = tool.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DgNavyPrimary,
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFF1F5F9),
                    labelColor = Color(0xFF334155)
                ),
                border = null,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

// ==========================================
// A. IMAGE TO PDF
// ==========================================

@Composable
private fun ImageToPdfContent(
    state: PdfUiState,
    onPickImages: () -> Unit,
    onRemoveImage: (Int) -> Unit,
    onMoveImage: (Int, Int) -> Unit,
    onRotateImage: (Int) -> Unit,
    onSetPageSize: (PdfPageSize) -> Unit,
    onSetOrientation: (PdfPageOrientation) -> Unit,
    onSetMargin: (PdfPageMargin) -> Unit,
    onSetFitMode: (PdfFitMode) -> Unit,
    onGenerate: () -> Unit,
    onDownload: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Combine Multiple Images into PDF",
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Supports JPG, PNG, and WebP certificates & photos. Reorder & rotate before building.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    DgPrimaryButton(
                        text = "Select Images (${state.imagesList.size} added)",
                        onClick = onPickImages,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_select_images"),
                        leadingIcon = Icons.Default.AddPhotoAlternate
                    )
                }
            }
        }

        // Image List with Drag / Move / Rotate
        if (state.imagesList.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Selected Images (${state.imagesList.size})",
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Use ⇄ arrows to reorder pages",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            itemsIndexed(state.imagesList) { index, item ->
                ImageRowCard(
                    index = index,
                    total = state.imagesList.size,
                    item = item,
                    onMoveUp = { onMoveImage(index, index - 1) },
                    onMoveDown = { onMoveImage(index, index + 1) },
                    onRotate = { onRotateImage(index) },
                    onRemove = { onRemoveImage(index) }
                )
            }

            // Page Layout Configuration
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Page Settings",
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark,
                            fontSize = 14.sp
                        )

                        // Page Size
                        Column {
                            Text(text = "Page Size", fontSize = 12.sp, color = Color(0xFF475569))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PdfPageSize.values().forEach { size ->
                                    FilterChip(
                                        selected = state.pageSize == size,
                                        onClick = { onSetPageSize(size) },
                                        label = { Text(size.label, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }

                        // Orientation
                        Column {
                            Text(text = "Orientation", fontSize = 12.sp, color = Color(0xFF475569))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PdfPageOrientation.values().forEach { orient ->
                                    FilterChip(
                                        selected = state.pageOrientation == orient,
                                        onClick = { onSetOrientation(orient) },
                                        label = { Text(orient.label, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }

                        // Margins
                        Column {
                            Text(text = "Margins", fontSize = 12.sp, color = Color(0xFF475569))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PdfPageMargin.values().forEach { margin ->
                                    FilterChip(
                                        selected = state.pageMargin == margin,
                                        onClick = { onSetMargin(margin) },
                                        label = { Text(margin.label, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }

                        // Fit Mode
                        Column {
                            Text(text = "Image Sizing", fontSize = 12.sp, color = Color(0xFF475569))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PdfFitMode.values().forEach { mode ->
                                    FilterChip(
                                        selected = state.fitMode == mode,
                                        onClick = { onSetFitMode(mode) },
                                        label = { Text(mode.label, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DgPrimaryButton(
                        text = "Generate PDF Document",
                        onClick = onGenerate,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_generate_pdf"),
                        leadingIcon = Icons.Default.PictureAsPdf
                    )

                    if (state.generatedImagePdfBytes != null) {
                        DgOutlinedButton(
                            text = "Download Generated PDF (${state.generatedImagePdfBytes.size / 1024} KB)",
                            onClick = onDownload,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_download_image_pdf"),
                            leadingIcon = Icons.Default.Download
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageRowCard(
    index: Int,
    total: Int,
    item: PdfImageItem,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRotate: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Index badge
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(DgNavyPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Thumbnail
            Image(
                bitmap = item.bitmap.asImageBitmap(),
                contentDescription = "Thumb",
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .graphicsLayer(rotationZ = item.rotationDegrees.toFloat()),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Page ${index + 1}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = DgNavyDark
                )
                Text(
                    text = "${item.originalWidth} × ${item.originalHeight} px • ${item.rotationDegrees}°",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Move controls
            IconButton(
                onClick = onMoveUp,
                enabled = index > 0,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Move Up",
                    tint = if (index > 0) DgNavyDark else Color(0xFFCBD5E1)
                )
            }

            IconButton(
                onClick = onMoveDown,
                enabled = index < total - 1,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Move Down",
                    tint = if (index < total - 1) DgNavyDark else Color(0xFFCBD5E1)
                )
            }

            IconButton(
                onClick = onRotate,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RotateRight,
                    contentDescription = "Rotate",
                    tint = DgNavyPrimary
                )
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = Color(0xFFEF4444)
                )
            }
        }
    }
}

// ==========================================
// B. PDF COMPRESSOR
// ==========================================

@Composable
private fun CompressorContent(
    state: PdfUiState,
    onPickPdf: () -> Unit,
    onSetLevel: (PdfCompressionLevel) -> Unit,
    onCompress: () -> Unit,
    onDownload: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PDF File Compressor",
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Reduce file size for SSC, UPSC, or State recruitment portals requiring < 200KB or 300KB uploads.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    DgPrimaryButton(
                        text = if (state.compressorSource != null) "Change PDF (${state.compressorSource.fileName})" else "Select PDF File",
                        onClick = onPickPdf,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_select_compress_pdf"),
                        leadingIcon = Icons.Default.PictureAsPdf
                    )
                }
            }
        }

        if (state.compressorSource != null) {
            val src = state.compressorSource
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        src.thumbnail?.let { bmp ->
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Thumb",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = src.fileName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DgNavyDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Original Size: ${src.fileSizeBytes / 1024} KB • ${src.pageCount} page(s)",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            // Compression Level Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Select Compression Level",
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark,
                            fontSize = 14.sp
                        )

                        PdfCompressionLevel.values().forEach { level ->
                            val isSelected = state.compressionLevel == level
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSetLevel(level) },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) DgBlueLight else Color(0xFFF8FAFC)
                                ),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(
                                        if (isSelected) DgNavyPrimary else DgBorderLight
                                    )
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { onSetLevel(level) },
                                        colors = RadioButtonDefaults.colors(selectedColor = DgNavyPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = level.label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = DgNavyDark
                                        )
                                        Text(
                                            text = level.description,
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                DgPrimaryButton(
                    text = "Compress PDF Document",
                    onClick = onCompress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_run_compress"),
                    leadingIcon = Icons.Default.Compress
                )
            }

            // Results Card
            if (state.compressionResult != null) {
                val res = state.compressionResult
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF86EFAC)))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Compression Output",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF166534)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(DgEmerald)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "-${res.reductionPercentage}% Size",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Original Size", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text(
                                        text = "${res.originalSizeBytes / 1024} KB",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF475569)
                                    )
                                }

                                Text(text = "➜", fontSize = 18.sp, color = DgNavyPrimary, modifier = Modifier.align(Alignment.CenterVertically))

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Compressed Size", fontSize = 11.sp, color = Color(0xFF166534))
                                    Text(
                                        text = "${res.outputSizeBytes / 1024} KB",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }

                            Text(
                                text = res.explanationNotice,
                                fontSize = 11.sp,
                                color = Color(0xFF166534),
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            DgPrimaryButton(
                                text = "Download Compressed PDF (${res.outputSizeBytes / 1024} KB)",
                                onClick = onDownload,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_download_compressed_pdf"),
                                leadingIcon = Icons.Default.Download
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// C. MERGE PDF
// ==========================================

@Composable
private fun MergePdfContent(
    state: PdfUiState,
    onPickPdfs: () -> Unit,
    onRemove: (Int) -> Unit,
    onMove: (Int, Int) -> Unit,
    onMerge: () -> Unit,
    onDownload: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Merge Multiple PDF Documents",
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Combine marks sheets, degree certificates, experience letters into a single PDF document.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    DgPrimaryButton(
                        text = "Add PDF Files (${state.mergeFilesList.size} added)",
                        onClick = onPickPdfs,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_add_merge_pdfs"),
                        leadingIcon = Icons.Default.Add
                    )
                }
            }
        }

        if (state.mergeFilesList.isNotEmpty()) {
            item {
                Text(
                    text = "Merge Queue (Order of documents)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DgNavyDark
                )
            }

            itemsIndexed(state.mergeFilesList) { index, file ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(DgNavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = file.fileName,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = DgNavyDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${file.fileSizeBytes / 1024} KB • ${file.pageCount} page(s)",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        IconButton(
                            onClick = { onMove(index, index - 1) },
                            enabled = index > 0,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Up",
                                tint = if (index > 0) DgNavyDark else Color(0xFFCBD5E1)
                            )
                        }

                        IconButton(
                            onClick = { onMove(index, index + 1) },
                            enabled = index < state.mergeFilesList.size - 1,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Down",
                                tint = if (index < state.mergeFilesList.size - 1) DgNavyDark else Color(0xFFCBD5E1)
                            )
                        }

                        IconButton(
                            onClick = { onRemove(index) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove",
                                tint = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DgPrimaryButton(
                        text = "Merge ${state.mergeFilesList.size} Documents",
                        onClick = onMerge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_execute_merge"),
                        leadingIcon = Icons.Default.PictureAsPdf
                    )

                    if (state.mergedPdfBytes != null) {
                        DgOutlinedButton(
                            text = "Download Merged PDF (${state.mergedPdfBytes.size / 1024} KB)",
                            onClick = onDownload,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_download_merged_pdf"),
                            leadingIcon = Icons.Default.Download
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// D. SPLIT PDF
// ==========================================

@Composable
private fun SplitPdfContent(
    state: PdfUiState,
    onPickPdf: () -> Unit,
    onTogglePage: (Int) -> Unit,
    onSelectAll: (Boolean) -> Unit,
    onExtract: () -> Unit,
    onDownload: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Split & Extract PDF Pages",
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Choose individual pages from multi-page documents to create a separate standalone PDF.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    DgPrimaryButton(
                        text = if (state.splitSource != null) "Change PDF (${state.splitSource.fileName})" else "Select PDF to Split",
                        onClick = onPickPdf,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_select_split_pdf"),
                        leadingIcon = Icons.Default.PictureAsPdf
                    )
                }
            }
        }

        if (state.splitThumbnails.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tap Pages to Select (${state.splitSelectedPages.size}/${state.splitThumbnails.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DgNavyDark
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Select All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DgNavyPrimary,
                            modifier = Modifier
                                .clickable { onSelectAll(true) }
                                .padding(4.dp)
                        )
                        Text(text = "•", fontSize = 12.sp, color = Color(0xFF94A3B8))
                        Text(
                            text = "Clear",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFEF4444),
                            modifier = Modifier
                                .clickable { onSelectAll(false) }
                                .padding(4.dp)
                        )
                    }
                }
            }

            // Grid of Page Thumbnails
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(state.splitThumbnails) { index, page ->
                        val isSelected = index in state.splitSelectedPages
                        Box(
                            modifier = Modifier
                                .aspectRatio(0.75f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) DgNavyPrimary else Color(0xFFCBD5E1),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(Color.White)
                                .clickable { onTogglePage(index) }
                        ) {
                            Image(
                                bitmap = page.thumbnail.asImageBitmap(),
                                contentDescription = "Page ${index + 1}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )

                            // Page Number Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .padding(vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Page ${index + 1}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Selection Indicator
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(DgNavyPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DgPrimaryButton(
                        text = "Extract Selected Pages (${state.splitSelectedPages.size})",
                        onClick = onExtract,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_extract_split"),
                        leadingIcon = Icons.Default.PictureAsPdf
                    )

                    if (state.splitResultBytes != null) {
                        DgOutlinedButton(
                            text = "Download Extracted PDF (${state.splitResultBytes.size / 1024} KB)",
                            onClick = onDownload,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_download_split_pdf"),
                            leadingIcon = Icons.Default.Download
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// E. PDF PAGE REORDER
// ==========================================

@Composable
private fun ReorderPdfContent(
    state: PdfUiState,
    onPickPdf: () -> Unit,
    onMovePage: (Int, Int) -> Unit,
    onSave: () -> Unit,
    onDownload: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PDF Page Reorder",
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Rearrange the order of pages in your PDF document effortlessly.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    DgPrimaryButton(
                        text = if (state.reorderSource != null) "Change PDF (${state.reorderSource.fileName})" else "Select PDF Document",
                        onClick = onPickPdf,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_select_reorder_pdf"),
                        leadingIcon = Icons.Default.SwapVert
                    )
                }
            }
        }

        if (state.reorderPages.isNotEmpty()) {
            item {
                Text(
                    text = "Page Arrangement Order (${state.reorderPages.size} pages)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DgNavyDark
                )
            }

            itemsIndexed(state.reorderPages) { index, page ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(DgNavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Image(
                            bitmap = page.thumbnail.asImageBitmap(),
                            contentDescription = "Page",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Target Page ${index + 1}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = DgNavyDark
                            )
                            Text(
                                text = "Original source page: #${page.pageIndex + 1}",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        IconButton(
                            onClick = { onMovePage(index, index - 1) },
                            enabled = index > 0,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Move Up",
                                tint = if (index > 0) DgNavyDark else Color(0xFFCBD5E1)
                            )
                        }

                        IconButton(
                            onClick = { onMovePage(index, index + 1) },
                            enabled = index < state.reorderPages.size - 1,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Move Down",
                                tint = if (index < state.reorderPages.size - 1) DgNavyDark else Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DgPrimaryButton(
                        text = "Build Reordered PDF",
                        onClick = onSave,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_build_reordered_pdf"),
                        leadingIcon = Icons.Default.PictureAsPdf
                    )

                    if (state.reorderedResultBytes != null) {
                        DgOutlinedButton(
                            text = "Download Reordered PDF (${state.reorderedResultBytes.size / 1024} KB)",
                            onClick = onDownload,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_download_reordered_pdf"),
                            leadingIcon = Icons.Default.Download
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// F. PDF TO IMAGES
// ==========================================

@Composable
private fun PdfToImagesContent(
    state: PdfUiState,
    onPickPdf: () -> Unit,
    onSetFormat: (String) -> Unit,
    onDownloadAll: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Convert PDF Document to Images",
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Extract each page of your PDF into high-clarity JPG or PNG pictures.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    DgPrimaryButton(
                        text = if (state.pdfToImgSource != null) "Change PDF (${state.pdfToImgSource.fileName})" else "Select PDF File",
                        onClick = onPickPdf,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_select_pdf_to_images"),
                        leadingIcon = Icons.Default.Collections
                    )
                }
            }
        }

        if (state.convertedImages.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Output Image Format",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DgNavyDark
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = state.imageFormat == "JPEG",
                                onClick = { onSetFormat("JPEG") },
                                label = { Text("JPG", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = state.imageFormat == "PNG",
                                onClick = { onSetFormat("PNG") },
                                label = { Text("PNG", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Extracted Page Images (${state.convertedImages.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DgNavyDark
                )
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(state.convertedImages) { idx, bmp ->
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                        ) {
                            Column {
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "Page ${idx + 1}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF8FAFC))
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Page ${idx + 1} (${bmp.width} × ${bmp.height})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = DgNavyDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                DgPrimaryButton(
                    text = "Download All Pages (ZIP Archive)",
                    onClick = onDownloadAll,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_download_images_zip"),
                    leadingIcon = Icons.Default.Download
                )
            }
        }
    }
}

// ==========================================
// G. PDF PREVIEW / VIEWER
// ==========================================

@Composable
private fun PdfViewerContent(
    state: PdfUiState,
    onPickPdf: () -> Unit,
    onSetPageIndex: (Int) -> Unit,
    onSetZoom: (Float) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Interactive PDF Preview & Inspector",
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Inspect file dimensions, navigate through multi-page documents, and zoom into fine text.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    DgPrimaryButton(
                        text = if (state.previewSource != null) "Open Different PDF" else "Open PDF Document",
                        onClick = onPickPdf,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_open_pdf_viewer"),
                        leadingIcon = Icons.Default.Visibility
                    )
                }
            }
        }

        if (state.previewPages.isNotEmpty() && state.previewSource != null) {
            val src = state.previewSource
            val curPage = state.previewPages.getOrNull(state.previewCurrentPageIndex)

            // Specs Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = src.fileName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DgNavyDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "File Size: ${src.fileSizeBytes / 1024} KB • Total: ${state.previewPages.size} pages",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        // Zoom Controls
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onSetZoom(state.previewZoomScale - 0.25f) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ZoomOut, "Zoom Out", tint = DgNavyDark)
                            }
                            Text(
                                text = "${(state.previewZoomScale * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DgNavyDark
                            )
                            IconButton(
                                onClick = { onSetZoom(state.previewZoomScale + 0.25f) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ZoomIn, "Zoom In", tint = DgNavyDark)
                            }
                        }
                    }
                }
            }

            // Page Render Canvas
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE2E8F0)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1)))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (curPage != null) {
                            Image(
                                bitmap = curPage.asImageBitmap(),
                                contentDescription = "Page Render",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                                    .graphicsLayer(
                                        scaleX = state.previewZoomScale,
                                        scaleY = state.previewZoomScale
                                    ),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            // Page Navigation Controls
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onSetPageIndex(state.previewCurrentPageIndex - 1) },
                            enabled = state.previewCurrentPageIndex > 0
                        ) {
                            Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Prev", fontSize = 12.sp)
                        }

                        Text(
                            text = "Page ${state.previewCurrentPageIndex + 1} of ${state.previewPages.size}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DgNavyDark
                        )

                        OutlinedButton(
                            onClick = { onSetPageIndex(state.previewCurrentPageIndex + 1) },
                            enabled = state.previewCurrentPageIndex < state.previewPages.size - 1
                        ) {
                            Text("Next", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}
