package com.example.jobphoto.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.editor.engine.ImageEngine
import com.example.editor.model.ImageMetadata
import com.example.jobphoto.data.InMemoryRecruitmentProfileRepository
import com.example.jobphoto.data.RecruitmentProfileRepository
import com.example.jobphoto.data.RecruitmentProfilesData
import com.example.jobphoto.engine.JobPhotoEngine
import com.example.jobphoto.model.JobPhotoValidationReport
import com.example.jobphoto.model.RecruitmentCategory
import com.example.jobphoto.model.RecruitmentProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class JobPhotoUiState(
    val selectedProfile: RecruitmentProfile = RecruitmentProfilesData.verifiedProfiles.first(),
    val searchQuery: String = "",
    val selectedCategory: RecruitmentCategory = RecruitmentCategory.ALL,
    val availableProfiles: List<RecruitmentProfile> = RecruitmentProfilesData.verifiedProfiles,
    val isRecruitmentPickerOpen: Boolean = false,

    // Photo State
    val originalPhotoBitmap: Bitmap? = null,
    val originalPhotoMetadata: ImageMetadata? = null,
    val processedPhotoBitmap: Bitmap? = null,
    val processedPhotoBytes: ByteArray? = null,
    val isPhotoAutoFixed: Boolean = false,
    val showPhotoBeforeAfter: Boolean = false,

    // Signature State
    val originalSigBitmap: Bitmap? = null,
    val processedSigBitmap: Bitmap? = null,
    val processedSigBytes: ByteArray? = null,
    val isSigAutoFixed: Boolean = false,
    val showSigBeforeAfter: Boolean = false,

    // Status & Progress
    val isProcessing: Boolean = false,
    val processingStatus: String = "",
    val toastNotification: String? = null,

    // Validation
    val validationReport: JobPhotoValidationReport? = null
)

class JobPhotoSignatureViewModel(
    private val repository: RecruitmentProfileRepository = InMemoryRecruitmentProfileRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobPhotoUiState())
    val uiState: StateFlow<JobPhotoUiState> = _uiState.asStateFlow()

    init {
        // Observe search and category filtering
        combine(
            repository.getAllProfiles(),
            _uiState
        ) { allProfiles, state ->
            val query = state.searchQuery.trim().lowercase()
            val category = state.selectedCategory
            allProfiles.filter { profile ->
                val matchesCategory = category == RecruitmentCategory.ALL || profile.category == category
                val matchesQuery = query.isBlank() ||
                        profile.name.lowercase().contains(query) ||
                        profile.organization.lowercase().contains(query) ||
                        profile.category.displayName.lowercase().contains(query) ||
                        profile.officialSource.notificationTitleOrNumber.lowercase().contains(query)
                matchesCategory && matchesQuery
            }
        }.onEach { filtered ->
            _uiState.update { it.copy(availableProfiles = filtered) }
        }.launchIn(viewModelScope)

        // Initialize validation report
        updateValidation()
    }

    fun openRecruitmentPicker() {
        _uiState.update { it.copy(isRecruitmentPickerOpen = true) }
    }

    fun closeRecruitmentPicker() {
        _uiState.update { it.copy(isRecruitmentPickerOpen = false) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectCategory(category: RecruitmentCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectProfile(profile: RecruitmentProfile) {
        _uiState.update {
            it.copy(
                selectedProfile = profile,
                isRecruitmentPickerOpen = false
            )
        }
        // If images already present, auto re-adjust if requested
        val state = _uiState.value
        if (state.originalPhotoBitmap != null && state.isPhotoAutoFixed) {
            autoFixPhoto()
        }
        if (state.originalSigBitmap != null && state.isSigAutoFixed) {
            autoFixSignature()
        }
        updateValidation()
    }

    fun loadPhotoFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, processingStatus = "Loading photo...") }
            val result = ImageEngine.loadBitmapFromUri(context, uri)
            if (result != null) {
                val (bitmap, metadata) = result
                _uiState.update {
                    it.copy(
                        originalPhotoBitmap = bitmap,
                        originalPhotoMetadata = metadata,
                        processedPhotoBitmap = bitmap,
                        processedPhotoBytes = null,
                        isPhotoAutoFixed = false,
                        isProcessing = false
                    )
                }
                autoFixPhoto()
            } else {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        toastNotification = "Unable to read selected photo."
                    )
                }
            }
        }
    }

    fun loadSamplePhoto() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, processingStatus = "Generating sample photo...") }
            val (bitmap, metadata) = ImageEngine.generateSampleCandidatePhoto()
            _uiState.update {
                it.copy(
                    originalPhotoBitmap = bitmap,
                    originalPhotoMetadata = metadata,
                    processedPhotoBitmap = bitmap,
                    processedPhotoBytes = null,
                    isPhotoAutoFixed = false,
                    isProcessing = false
                )
            }
            autoFixPhoto()
        }
    }

    fun removePhoto() {
        _uiState.update {
            it.copy(
                originalPhotoBitmap = null,
                originalPhotoMetadata = null,
                processedPhotoBitmap = null,
                processedPhotoBytes = null,
                isPhotoAutoFixed = false
            )
        }
        updateValidation()
    }

    fun loadSignatureFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, processingStatus = "Loading signature...") }
            val result = ImageEngine.loadBitmapFromUri(context, uri)
            if (result != null) {
                val (bitmap, _) = result
                _uiState.update {
                    it.copy(
                        originalSigBitmap = bitmap,
                        processedSigBitmap = bitmap,
                        processedSigBytes = null,
                        isSigAutoFixed = false,
                        isProcessing = false
                    )
                }
                autoFixSignature()
            } else {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        toastNotification = "Unable to read signature image."
                    )
                }
            }
        }
    }

    fun loadSampleSignature() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, processingStatus = "Generating sample signature...") }
            val bitmap = JobPhotoEngine.generateSampleSignature()
            _uiState.update {
                it.copy(
                    originalSigBitmap = bitmap,
                    processedSigBitmap = bitmap,
                    processedSigBytes = null,
                    isSigAutoFixed = false,
                    isProcessing = false
                )
            }
            autoFixSignature()
        }
    }

    fun removeSignature() {
        _uiState.update {
            it.copy(
                originalSigBitmap = null,
                processedSigBitmap = null,
                processedSigBytes = null,
                isSigAutoFixed = false
            )
        }
        updateValidation()
    }

    fun togglePhotoBeforeAfter() {
        _uiState.update { it.copy(showPhotoBeforeAfter = !it.showPhotoBeforeAfter) }
    }

    fun toggleSigBeforeAfter() {
        _uiState.update { it.copy(showSigBeforeAfter = !it.showSigBeforeAfter) }
    }

    fun autoFixPhoto() {
        val state = _uiState.value
        val source = state.originalPhotoBitmap ?: return
        val req = state.selectedProfile.photoRequirement

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, processingStatus = "Auto-fixing photo to ${req.width}×${req.height} px...") }
            try {
                val fixedBitmap = JobPhotoEngine.autoFixPhoto(source, req)
                val (compressedBytes, _) = JobPhotoEngine.compressToRange(
                    fixedBitmap,
                    req.minKb,
                    req.maxKb,
                    req.requiredFormat
                )

                _uiState.update {
                    it.copy(
                        processedPhotoBitmap = fixedBitmap,
                        processedPhotoBytes = compressedBytes,
                        isPhotoAutoFixed = true,
                        isProcessing = false
                    )
                }
                updateValidation()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        toastNotification = "Error fixing photo: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun autoFixSignature() {
        val state = _uiState.value
        val source = state.originalSigBitmap ?: return
        val req = state.selectedProfile.signatureRequirement

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, processingStatus = "Auto-fixing signature to ${req.width}×${req.height} px...") }
            try {
                val fixedBitmap = JobPhotoEngine.autoFixSignature(source, req)
                val (compressedBytes, _) = JobPhotoEngine.compressToRange(
                    fixedBitmap,
                    req.minKb,
                    req.maxKb,
                    req.requiredFormat
                )

                _uiState.update {
                    it.copy(
                        processedSigBitmap = fixedBitmap,
                        processedSigBytes = compressedBytes,
                        isSigAutoFixed = true,
                        isProcessing = false
                    )
                }
                updateValidation()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        toastNotification = "Error fixing signature: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    /**
     * Master "FIX AUTOMATICALLY" button:
     * Fixes both photo and signature (or whichever are loaded) to 100% match selected recruitment specs.
     * If neither are uploaded, loads samples and auto-fixes them.
     */
    fun fixAllAutomatically() {
        val state = _uiState.value
        if (state.originalPhotoBitmap == null && state.originalSigBitmap == null) {
            // Load both samples and fix
            viewModelScope.launch {
                _uiState.update { it.copy(isProcessing = true, processingStatus = "Loading candidate photo and signature...") }
                val (photoBmp, photoMeta) = ImageEngine.generateSampleCandidatePhoto()
                val sigBmp = JobPhotoEngine.generateSampleSignature()

                val fixedPhoto = JobPhotoEngine.autoFixPhoto(photoBmp, state.selectedProfile.photoRequirement)
                val (photoBytes, _) = JobPhotoEngine.compressToRange(
                    fixedPhoto,
                    state.selectedProfile.photoRequirement.minKb,
                    state.selectedProfile.photoRequirement.maxKb
                )

                val fixedSig = JobPhotoEngine.autoFixSignature(sigBmp, state.selectedProfile.signatureRequirement)
                val (sigBytes, _) = JobPhotoEngine.compressToRange(
                    fixedSig,
                    state.selectedProfile.signatureRequirement.minKb,
                    state.selectedProfile.signatureRequirement.maxKb
                )

                _uiState.update {
                    it.copy(
                        originalPhotoBitmap = photoBmp,
                        originalPhotoMetadata = photoMeta,
                        processedPhotoBitmap = fixedPhoto,
                        processedPhotoBytes = photoBytes,
                        isPhotoAutoFixed = true,
                        originalSigBitmap = sigBmp,
                        processedSigBitmap = fixedSig,
                        processedSigBytes = sigBytes,
                        isSigAutoFixed = true,
                        isProcessing = false,
                        toastNotification = "Sample photo & signature loaded and automatically fixed to ${state.selectedProfile.organization} specs!"
                    )
                }
                updateValidation()
            }
        } else {
            // Fix existing
            if (state.originalPhotoBitmap != null) {
                autoFixPhoto()
            }
            if (state.originalSigBitmap != null) {
                autoFixSignature()
            }
        }
    }

    fun downloadPhoto(context: Context) {
        val state = _uiState.value
        val bytes = state.processedPhotoBytes ?: return
        val org = state.selectedProfile.organization.replace(" ", "_").take(15)

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, processingStatus = "Saving photo...") }
            val uri = JobPhotoEngine.saveSingleFileToDevice(
                context = context,
                bytes = bytes,
                filenameWithExt = "DG_with_Anup_${org}_Photo.jpg"
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    toastNotification = if (uri != null) "Photo saved to Pictures/DG_with_Anup!" else "Failed to save photo."
                )
            }
        }
    }

    fun downloadSignature(context: Context) {
        val state = _uiState.value
        val bytes = state.processedSigBytes ?: return
        val org = state.selectedProfile.organization.replace(" ", "_").take(15)

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, processingStatus = "Saving signature...") }
            val uri = JobPhotoEngine.saveSingleFileToDevice(
                context = context,
                bytes = bytes,
                filenameWithExt = "DG_with_Anup_${org}_Signature.jpg"
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    toastNotification = if (uri != null) "Signature saved to Pictures/DG_with_Anup!" else "Failed to save signature."
                )
            }
        }
    }

    fun downloadAllZip(context: Context) {
        val state = _uiState.value
        val photoBytes = state.processedPhotoBytes
        val sigBytes = state.processedSigBytes

        if (photoBytes == null && sigBytes == null) {
            _uiState.update { it.copy(toastNotification = "Please process photo or signature before downloading kit.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, processingStatus = "Creating Application Kit ZIP...") }
            try {
                val zipBytes = JobPhotoEngine.createApplicationKitZip(
                    photoBytes = photoBytes,
                    sigBytes = sigBytes,
                    profile = state.selectedProfile
                )
                val uri = JobPhotoEngine.saveZipToDevice(
                    context = context,
                    bytes = zipBytes,
                    recruitmentName = state.selectedProfile.name
                )
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        toastNotification = if (uri != null) "Application Kit saved to Downloads/DG_with_Anup!" else "Failed to save ZIP kit."
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        toastNotification = "Error creating kit: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun clearNotification() {
        _uiState.update { it.copy(toastNotification = null) }
    }

    private fun updateValidation() {
        val state = _uiState.value
        val report = JobPhotoEngine.validateJobSubmission(
            photoBitmap = state.processedPhotoBitmap,
            photoBytes = state.processedPhotoBytes,
            photoReq = state.selectedProfile.photoRequirement,
            sigBitmap = state.processedSigBitmap,
            sigBytes = state.processedSigBytes,
            sigReq = state.selectedProfile.signatureRequirement
        )
        _uiState.update { it.copy(validationReport = report) }
    }
}
