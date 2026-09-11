package com.example.jobphoto.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jobphoto.ui.components.DisclaimerCard
import com.example.jobphoto.ui.components.JobValidationChecklistCard
import com.example.jobphoto.ui.components.MasterActionPanel
import com.example.jobphoto.ui.components.PhotoProcessingCard
import com.example.jobphoto.ui.components.RecruitmentSelectorSheet
import com.example.jobphoto.ui.components.RequirementProfileCard
import com.example.jobphoto.ui.components.SignatureProcessingCard
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavy
import com.example.ui.theme.DgSaffron

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobPhotoSignatureScreen(
    onBack: () -> Unit,
    viewModel: JobPhotoSignatureViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.toastNotification) {
        state.toastNotification?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Govt Job Photo & Signature",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = DgNavy
                        )
                        Text(
                            text = "DG with Anup Spec Compliance Engine",
                            fontSize = 11.sp,
                            color = DgSaffron,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back_job_photo")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DgNavy
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openRecruitmentPicker() },
                        modifier = Modifier.testTag("btn_top_select_recruitment")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Select Exam",
                            tint = DgSaffron
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF8FAFC),
        modifier = Modifier.testTag("screen_job_photo_signature")
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 1. Hero Brand Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(DgNavy, Color(0xFF1E293B))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(DgSaffron, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("DG", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DG with Anup Govt Portal Standards",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFCD34D)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Automatic Crop, Resize, Whitening & Compression to exact official notification guidelines. Never get rejected for incorrect photo or signature specs.",
                            fontSize = 12.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // 2. Recruitment Requirement Profile Card
            item {
                RequirementProfileCard(
                    profile = state.selectedProfile,
                    onChangeExamClick = { viewModel.openRecruitmentPicker() }
                )
            }

            // 3. Master Action Panel (Fix Automatically + Download All ZIP)
            item {
                MasterActionPanel(
                    isProcessing = state.isProcessing,
                    processingStatus = state.processingStatus,
                    hasPhoto = state.processedPhotoBytes != null,
                    hasSignature = state.processedSigBytes != null,
                    onFixAllAutomatically = { viewModel.fixAllAutomatically() },
                    onDownloadAllZip = { viewModel.downloadAllZip(context) },
                    onDownloadPhoto = { viewModel.downloadPhoto(context) },
                    onDownloadSignature = { viewModel.downloadSignature(context) }
                )
            }

            // 4. Candidate Photograph Processing Card
            item {
                PhotoProcessingCard(
                    originalBitmap = state.originalPhotoBitmap,
                    processedBitmap = state.processedPhotoBitmap,
                    processedBytes = state.processedPhotoBytes,
                    requirement = state.selectedProfile.photoRequirement,
                    isAutoFixed = state.isPhotoAutoFixed,
                    showBeforeAfter = state.showPhotoBeforeAfter,
                    isProcessing = state.isProcessing,
                    onPhotoSelected = { uri -> viewModel.loadPhotoFromUri(context, uri) },
                    onSampleClick = { viewModel.loadSamplePhoto() },
                    onRemoveClick = { viewModel.removePhoto() },
                    onAutoFixClick = { viewModel.autoFixPhoto() },
                    onToggleBeforeAfter = { viewModel.togglePhotoBeforeAfter() },
                    onDownloadClick = { viewModel.downloadPhoto(context) }
                )
            }

            // 5. Candidate Signature Processing Card
            item {
                SignatureProcessingCard(
                    originalBitmap = state.originalSigBitmap,
                    processedBitmap = state.processedSigBitmap,
                    processedBytes = state.processedSigBytes,
                    requirement = state.selectedProfile.signatureRequirement,
                    isAutoFixed = state.isSigAutoFixed,
                    showBeforeAfter = state.showSigBeforeAfter,
                    isProcessing = state.isProcessing,
                    onSignatureSelected = { uri -> viewModel.loadSignatureFromUri(context, uri) },
                    onSampleClick = { viewModel.loadSampleSignature() },
                    onRemoveClick = { viewModel.removeSignature() },
                    onAutoFixClick = { viewModel.autoFixSignature() },
                    onToggleBeforeAfter = { viewModel.toggleSigBeforeAfter() },
                    onDownloadClick = { viewModel.downloadSignature(context) }
                )
            }

            // 6. Validation Checklist Card
            item {
                JobValidationChecklistCard(
                    report = state.validationReport
                )
            }

            // 7. Disclaimer & Privacy Guarantee
            item {
                DisclaimerCard()
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Recruitment Selection Bottom Sheet
        RecruitmentSelectorSheet(
            isOpen = state.isRecruitmentPickerOpen,
            profiles = state.availableProfiles,
            selectedProfile = state.selectedProfile,
            searchQuery = state.searchQuery,
            selectedCategory = state.selectedCategory,
            onSearchChange = { viewModel.setSearchQuery(it) },
            onCategorySelect = { viewModel.selectCategory(it) },
            onProfileSelect = { viewModel.selectProfile(it) },
            onDismiss = { viewModel.closeRecruitmentPicker() }
        )
    }
}
