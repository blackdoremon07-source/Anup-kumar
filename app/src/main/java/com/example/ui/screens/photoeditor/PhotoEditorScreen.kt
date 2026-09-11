package com.example.ui.screens.photoeditor

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.editor.model.AspectRatioPreset
import com.example.editor.model.FitMode
import com.example.editor.model.NormalizedCropRect
import com.example.ui.components.editor.AdjustmentSliders
import com.example.ui.components.editor.BackgroundSelector
import com.example.ui.components.editor.BeforeAfterViewer
import com.example.ui.components.editor.FilterPresetSelector
import com.example.ui.components.editor.InteractiveCropCanvas
import com.example.ui.components.editor.SizeOptimizerCard
import com.example.ui.components.editor.ValidationPanel
import com.example.ui.theme.DgAmberLight
import com.example.ui.theme.DgBackgroundLight
import com.example.ui.theme.DgBorderLight
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoEditorScreen(
    onBack: () -> Unit,
    viewModel: PhotoEditorViewModel = viewModel()
) {
    val context = LocalContext.current

    val originalBitmap by viewModel.originalBitmap.collectAsState()
    val previewBitmap by viewModel.previewBitmap.collectAsState()
    val originalMetadata by viewModel.originalMetadata.collectAsState()
    val currentState by viewModel.currentState.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val estimatedSizeBytes by viewModel.estimatedSizeBytes.collectAsState()
    val targetResult by viewModel.targetSizeResult.collectAsState()
    val validationReport by viewModel.validationReport.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }
    var isCropModeActive by remember { mutableStateOf(false) }

    // Manual dimension input states
    var widthInput by remember(currentState.targetWidth) { mutableStateOf(currentState.targetWidth.toString()) }
    var heightInput by remember(currentState.targetHeight) { mutableStateOf(currentState.targetHeight.toString()) }

    // Photo pickers
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.loadImageFromUri(context, uri)
        }
    }

    val fallbackPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.loadImageFromUri(context, uri)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is EditorUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is EditorUiEvent.DownloadSuccess -> {
                    Toast.makeText(
                        context,
                        "Photo saved successfully! / फोटो डाउनलोड हो गई!\n${event.path}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DgBackgroundLight)
            .statusBarsPadding()
            .testTag("screen_photo_editor")
    ) {
        // ==========================================
        // 1. TOP APP BAR (Header with Undo/Redo/Reset)
        // ==========================================
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("editor_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DgNavyPrimary
                        )
                    }

                    Column {
                        Text(
                            text = "Photo Editor",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = DgNavyDark
                        )
                        Text(
                            text = "DG with Anup Suite",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Undo
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = viewModel.canUndo(),
                        modifier = Modifier.testTag("editor_undo_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (viewModel.canUndo()) DgNavyPrimary else Color(0xFFCBD5E1)
                        )
                    }

                    // Redo
                    IconButton(
                        onClick = { viewModel.redo() },
                        enabled = viewModel.canRedo(),
                        modifier = Modifier.testTag("editor_redo_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (viewModel.canRedo()) DgNavyPrimary else Color(0xFFCBD5E1)
                        )
                    }

                    // Reset Everything
                    IconButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.testTag("editor_reset_all_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset All",
                            tint = Color(0xFFDC2626)
                        )
                    }
                }
            }
        }

        // ==========================================
        // MAIN SCROLLABLE EDITOR WORKSPACE
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Privacy & Local Processing Notice
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBF7D0)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = DgEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "100% Local On-Device Processing. No photos are uploaded to any server.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF166534)
                    )
                }
            }

            // ==========================================
            // 2. PHOTO UPLOAD SECTION
            // ==========================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                            text = "Photo Upload / फोटो अपलोड",
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "JPG, JPEG, PNG, WebP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DgSaffron
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Choose / Change Photo
                        Button(
                            onClick = {
                                try {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } catch (e: Exception) {
                                    fallbackPickerLauncher.launch("image/*")
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("choose_photo_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = DgNavyPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (originalBitmap == null) "Choose Photo" else "Change Photo",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Sample Photo
                        OutlinedButton(
                            onClick = { viewModel.loadSamplePhoto() },
                            modifier = Modifier.weight(0.9f).testTag("sample_photo_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Photo, contentDescription = null, modifier = Modifier.size(16.dp), tint = DgNavyPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sample", fontSize = 12.sp, color = DgNavyPrimary, fontWeight = FontWeight.SemiBold)
                        }

                        // Remove Photo
                        if (originalBitmap != null) {
                            OutlinedButton(
                                onClick = { viewModel.removePhoto() },
                                modifier = Modifier.weight(0.7f).testTag("remove_photo_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 3. IMAGE PREVIEW (Main Stage)
            // ==========================================
            if (previewBitmap != null) {
                val currentBmp = previewBitmap!!
                val bmpRatio = if (currentBmp.height > 0) {
                    (currentBmp.width.toFloat() / currentBmp.height.toFloat()).coerceIn(0.5f, 2.0f)
                } else 1f

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title row with interactive crop launcher
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Preview Workspace / पूर्वावलोकन",
                                fontWeight = FontWeight.Bold,
                                color = DgNavyDark,
                                fontSize = 14.sp
                            )

                            OutlinedButton(
                                onClick = { isCropModeActive = !isCropModeActive },
                                shape = RoundedCornerShape(8.dp),
                                colors = if (isCropModeActive) ButtonDefaults.outlinedButtonColors(containerColor = DgSaffron, contentColor = Color.White) else ButtonDefaults.outlinedButtonColors(),
                                modifier = Modifier.height(32.dp).testTag("toggle_crop_mode")
                            ) {
                                Icon(Icons.Default.Crop, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isCropModeActive) "Close Crop" else "Crop Tool", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // If Crop Tool is active, show interactive crop canvas
                        if (isCropModeActive && originalBitmap != null) {
                            InteractiveCropCanvas(
                                bitmap = originalBitmap!!,
                                initialCropRect = currentState.cropRect,
                                onApplyCrop = { newCrop ->
                                    viewModel.updateCropRect(newCrop)
                                    isCropModeActive = false
                                },
                                onCancel = { isCropModeActive = false }
                            )
                        } else {
                            // Standard Live Preview Viewport
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(bmpRatio)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = remember(currentBmp) { currentBmp.asImageBitmap() },
                                    contentDescription = "Edited Image Preview",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Dimensions & Size Badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .background(Color(0xCC000000), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "${currentBmp.width} × ${currentBmp.height} px  •  ${String.format("%.1f", estimatedSizeBytes / 1024f)} KB",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                if (isProcessing) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0x66000000)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = DgSaffron,
                                            modifier = Modifier.size(36.dp),
                                            strokeWidth = 3.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 4. IMAGE INFORMATION
                // ==========================================
                if (originalMetadata != null) {
                    val meta = originalMetadata!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Image Information / फोटो विवरण",
                                fontWeight = FontWeight.Bold,
                                color = DgNavyDark,
                                fontSize = 14.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                InfoPill(title = "Original Dimensions", value = "${meta.width} × ${meta.height} px", modifier = Modifier.weight(1f))
                                InfoPill(title = "Original File Size", value = "${String.format("%.1f", meta.fileSizeBytes / 1024f)} KB", modifier = Modifier.weight(1f))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                InfoPill(title = "File Format", value = meta.mimeType.substringAfter("/").uppercase(), modifier = Modifier.weight(1f))
                                InfoPill(title = "Color Depth", value = meta.colorDepth, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // ==========================================
                // 5. EXACT PIXEL SIZE & ASPECT RATIO LOCK
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Exact Pixel Dimensions / सटीक पिक्सेल साइज",
                                    fontWeight = FontWeight.Bold,
                                    color = DgNavyDark,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Custom width & height for online application forms",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (currentState.isAspectLocked) Color(0xFFEFF6FF) else Color(0xFFF1F5F9))
                                    .clickable { viewModel.setLockAspectRatio(!currentState.isAspectLocked) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (currentState.isAspectLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = if (currentState.isAspectLocked) DgNavyPrimary else Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentState.isAspectLocked) "Lock Ratio ON" else "Lock OFF",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentState.isAspectLocked) DgNavyPrimary else Color(0xFF64748B)
                                )
                            }
                        }

                        // Inputs: Width: ___ px, Height: ___ px
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = widthInput,
                                onValueChange = { input ->
                                    val filtered = input.filter { it.isDigit() }
                                    widthInput = filtered
                                    filtered.toIntOrNull()?.let { w ->
                                        if (w in 1..8000) {
                                            viewModel.updateWidthLocked(w)
                                        }
                                    }
                                },
                                label = { Text("Width (px)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("input_width_px")
                            )

                            Text("×", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))

                            OutlinedTextField(
                                value = heightInput,
                                onValueChange = { input ->
                                    val filtered = input.filter { it.isDigit() }
                                    heightInput = filtered
                                    filtered.toIntOrNull()?.let { h ->
                                        if (h in 1..8000) {
                                            viewModel.updateHeightLocked(h)
                                        }
                                    }
                                },
                                label = { Text("Height (px)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("input_height_px")
                            )
                        }

                        Text(
                            text = "Limits: Min 1 px • Max ${currentState.originalWidth}×${currentState.originalHeight} px (Original)",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // ==========================================
                // 6. ASPECT RATIOS PRESETS
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Aspect Ratios / आस्पेक्ट अनुपात",
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Standard framing presets (Keeps subject centered)",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AspectRatioPreset.values().forEach { preset ->
                                val isSelected = currentState.selectedAspectRatioPreset == preset
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) DgNavyPrimary else Color(0xFFF8FAFC))
                                        .border(1.dp, if (isSelected) DgNavyPrimary else Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setAspectRatioPreset(preset) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = preset.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else DgNavyDark
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 7. RESIZE CONTROLS (Percentage & Presets)
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Percentage Resize / प्रतिशत रीसाइज",
                                    fontWeight = FontWeight.Bold,
                                    color = DgNavyDark,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Scale resolution cleanly without distortion",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "${currentState.resizePercentage}%",
                                fontWeight = FontWeight.Bold,
                                color = DgSaffron,
                                fontSize = 14.sp
                            )
                        }

                        // Presets: 25%, 50%, 75%, 100%
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(25, 50, 75, 100).forEach { pct ->
                                val isSelected = currentState.resizePercentage == pct
                                OutlinedButton(
                                    onClick = { viewModel.setResizePercentage(pct) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    colors = if (isSelected) ButtonDefaults.outlinedButtonColors(containerColor = DgNavyPrimary, contentColor = Color.White) else ButtonDefaults.outlinedButtonColors()
                                ) {
                                    Text("$pct%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Slider(
                            value = currentState.resizePercentage.toFloat(),
                            onValueChange = { viewModel.setResizePercentage(it.toInt()) },
                            valueRange = 1f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = DgNavyPrimary,
                                activeTrackColor = DgNavyPrimary,
                                inactiveTrackColor = Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.fillMaxWidth().height(24.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Original: ${currentState.originalWidth} × ${currentState.originalHeight} px", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text("New: ${currentState.targetWidth} × ${currentState.targetHeight} px", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DgNavyDark)
                        }
                    }
                }

                // ==========================================
                // 8. FIT / FILL / CROP MODES
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Framing Mode (Fit / Fill / Crop)",
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark,
                            fontSize = 15.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FitMode.values().forEach { mode ->
                                val isSelected = currentState.fitMode == mode
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) DgNavyPrimary else Color(0xFFF8FAFC))
                                        .border(1.dp, if (isSelected) DgNavyPrimary else Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setFitMode(mode) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = mode.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else DgNavyDark
                                    )
                                }
                            }
                        }

                        // Explanatory note
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "${currentState.fitMode.title}: ${currentState.fitMode.description}",
                                fontSize = 11.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }

                // ==========================================
                // 9. ROTATE & FLIP
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                                text = "Rotate & Flip / घुमाएं एवं पलटें",
                                fontWeight = FontWeight.Bold,
                                color = DgNavyDark,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Angle: ${currentState.rotationDegrees}°",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DgSaffron
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.rotateLeft() },
                                modifier = Modifier.weight(1f).testTag("rotate_left_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.RotateLeft, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Left 90°", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.rotateRight() },
                                modifier = Modifier.weight(1f).testTag("rotate_right_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.RotateRight, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Right 90°", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.flipHorizontal() },
                                modifier = Modifier.weight(1f).testTag("flip_h_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Flip, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Flip H", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.flipVertical() },
                                modifier = Modifier.weight(1f).testTag("flip_v_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Flip, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Flip V", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // ==========================================
                // 10. IMAGE ADJUSTMENTS SLIDERS
                // ==========================================
                AdjustmentSliders(
                    adjustments = currentState.adjustments,
                    onAdjustmentsChanged = { viewModel.updateAdjustments(it) },
                    onResetAdjustments = { viewModel.resetAdjustments() }
                )

                // ==========================================
                // 11. FILTERS PRESETS
                // ==========================================
                FilterPresetSelector(
                    selectedFilter = currentState.filter,
                    onFilterSelected = { viewModel.setFilter(it) }
                )

                // ==========================================
                // 12. BACKGROUND COLOR
                // ==========================================
                BackgroundSelector(
                    selectedBackground = currentState.backgroundColor,
                    outputFormat = currentState.outputFormat,
                    onBackgroundSelected = { viewModel.setBackgroundColor(it) }
                )

                // ==========================================
                // 13. FORMAT, QUALITY & TARGET SIZE OPTIMIZER
                // ==========================================
                SizeOptimizerCard(
                    outputFormat = currentState.outputFormat,
                    onFormatChanged = { viewModel.setOutputFormat(it) },
                    quality = currentState.quality,
                    onQualityChanged = { viewModel.setQuality(it) },
                    estimatedSizeBytes = estimatedSizeBytes,
                    isTargetSizeEnabled = currentState.isTargetSizeEnabled,
                    onToggleTargetSize = { viewModel.setTargetSizeEnabled(it) },
                    targetSizeKb = currentState.targetSizeKb,
                    onTargetSizeChanged = { viewModel.setTargetSizeKb(it) },
                    targetResult = targetResult
                )

                // ==========================================
                // 14. LIVE VALIDATION PANEL
                // ==========================================
                ValidationPanel(report = validationReport)

                // ==========================================
                // 15. BEFORE / AFTER COMPARISON
                // ==========================================
                BeforeAfterViewer(
                    originalBitmap = originalBitmap,
                    editedBitmap = previewBitmap
                )

                // ==========================================
                // 16. FINAL EXPORT SUMMARY & DOWNLOAD
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E36)), // DgNavyDark
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Download Summary / डाउनलोड सारांश",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SummaryTag("Final Dimensions", "${currentBmp.width} × ${currentBmp.height} px", Modifier.weight(1f))
                            SummaryTag("Aspect Ratio", "${String.format("%.2f", currentBmp.width.toFloat() / currentBmp.height.toFloat())}:1", Modifier.weight(1f))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SummaryTag("Final Format", currentState.outputFormat.name, Modifier.weight(1f))
                            SummaryTag("File Size", "${String.format("%.1f", estimatedSizeBytes / 1024f)} KB", Modifier.weight(1f))
                        }

                        Text(
                            text = "File will be saved to Pictures/DG_with_Anup as DG_with_Anup_Edited_Photo.${currentState.outputFormat.extension}",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )

                        Button(
                            onClick = { viewModel.downloadPhoto(context) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("download_photo_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = DgSaffron),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DOWNLOAD PHOTO / फोटो डाउनलोड करें",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // Empty State when photo removed
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, DgBorderLight, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No Photo Selected / कोई फोटो नहीं चुनी गई",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DgNavyDark
                        )
                        Text(
                            text = "Upload JPG, PNG, WebP or choose sample candidate photo",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Button(
                            onClick = { viewModel.loadSamplePhoto() },
                            colors = ButtonDefaults.buttonColors(containerColor = DgNavyPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Load Sample Photo")
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog for Reset Everything
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Everything? / सभी बदलाव रीसेट करें?") },
            text = { Text("This will restore the original unedited photo and reset all adjustments, crops, and resize settings.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetEverything()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset All", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun InfoPill(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column {
            Text(title, fontSize = 10.sp, color = Color(0xFF64748B))
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DgNavyDark)
        }
    }
}

@Composable
fun SummaryTag(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E293B))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column {
            Text(label, fontSize = 10.sp, color = Color(0xFF94A3B8))
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
