package com.example.jobphoto.data

import com.example.jobphoto.model.DocumentRequirement
import com.example.jobphoto.model.OfficialSource
import com.example.jobphoto.model.PhotoRequirement
import com.example.jobphoto.model.RecruitmentCategory
import com.example.jobphoto.model.RecruitmentProfile
import com.example.jobphoto.model.SignatureRequirement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

interface RecruitmentProfileRepository {
    fun getAllProfiles(): Flow<List<RecruitmentProfile>>
    fun getProfileById(id: String): Flow<RecruitmentProfile?>
    fun getProfilesByCategory(category: RecruitmentCategory): Flow<List<RecruitmentProfile>>
    fun searchProfiles(query: String, category: RecruitmentCategory = RecruitmentCategory.ALL): Flow<List<RecruitmentProfile>>

    // Admin Compatibility Methods (For Part 6 Admin Panel)
    fun addProfile(profile: RecruitmentProfile)
    fun updateProfile(profile: RecruitmentProfile)
    fun deleteProfile(id: String)
    fun updatePhotoRequirement(profileId: String, photoRequirement: PhotoRequirement)
    fun updateSignatureRequirement(profileId: String, signatureRequirement: SignatureRequirement)
    fun updateDocumentRequirement(profileId: String, documentRequirement: DocumentRequirement)
    fun updateOfficialSource(profileId: String, officialSource: OfficialSource)
    fun setVerificationStatus(profileId: String, isVerified: Boolean, verifiedDate: String)
}

class InMemoryRecruitmentProfileRepository : RecruitmentProfileRepository {

    private val _profiles = MutableStateFlow<List<RecruitmentProfile>>(RecruitmentProfilesData.verifiedProfiles)

    override fun getAllProfiles(): Flow<List<RecruitmentProfile>> = _profiles.asStateFlow()

    override fun getProfileById(id: String): Flow<RecruitmentProfile?> {
        return _profiles.map { list -> list.find { it.id == id } }
    }

    override fun getProfilesByCategory(category: RecruitmentCategory): Flow<List<RecruitmentProfile>> {
        return _profiles.map { list ->
            if (category == RecruitmentCategory.ALL) list
            else list.filter { it.category == category }
        }
    }

    override fun searchProfiles(
        query: String,
        category: RecruitmentCategory
    ): Flow<List<RecruitmentProfile>> {
        return _profiles.map { list ->
            val byCategory = if (category == RecruitmentCategory.ALL) list else list.filter { it.category == category }
            if (query.isBlank()) {
                byCategory
            } else {
                val q = query.trim().lowercase()
                byCategory.filter {
                    it.name.lowercase().contains(q) ||
                            it.organization.lowercase().contains(q) ||
                            it.category.displayName.lowercase().contains(q) ||
                            it.officialSource.notificationTitleOrNumber.lowercase().contains(q)
                }
            }
        }
    }

    // ==========================================
    // ADMIN CRUD OPERATIONS (Part 6 Ready)
    // ==========================================

    override fun addProfile(profile: RecruitmentProfile) {
        val current = _profiles.value.toMutableList()
        current.add(0, profile)
        _profiles.value = current
    }

    override fun updateProfile(profile: RecruitmentProfile) {
        val current = _profiles.value.toMutableList()
        val index = current.indexOfFirst { it.id == profile.id }
        if (index != -1) {
            current[index] = profile
            _profiles.value = current
        }
    }

    override fun deleteProfile(id: String) {
        val current = _profiles.value.toMutableList()
        current.removeAll { it.id == id }
        _profiles.value = current
    }

    override fun updatePhotoRequirement(profileId: String, photoRequirement: PhotoRequirement) {
        val current = _profiles.value.toMutableList()
        val index = current.indexOfFirst { it.id == profileId }
        if (index != -1) {
            current[index] = current[index].copy(photoRequirement = photoRequirement)
            _profiles.value = current
        }
    }

    override fun updateSignatureRequirement(
        profileId: String,
        signatureRequirement: SignatureRequirement
    ) {
        val current = _profiles.value.toMutableList()
        val index = current.indexOfFirst { it.id == profileId }
        if (index != -1) {
            current[index] = current[index].copy(signatureRequirement = signatureRequirement)
            _profiles.value = current
        }
    }

    override fun updateDocumentRequirement(
        profileId: String,
        documentRequirement: DocumentRequirement
    ) {
        val current = _profiles.value.toMutableList()
        val index = current.indexOfFirst { it.id == profileId }
        if (index != -1) {
            current[index] = current[index].copy(documentRequirement = documentRequirement)
            _profiles.value = current
        }
    }

    override fun updateOfficialSource(profileId: String, officialSource: OfficialSource) {
        val current = _profiles.value.toMutableList()
        val index = current.indexOfFirst { it.id == profileId }
        if (index != -1) {
            current[index] = current[index].copy(officialSource = officialSource)
            _profiles.value = current
        }
    }

    override fun setVerificationStatus(
        profileId: String,
        isVerified: Boolean,
        verifiedDate: String
    ) {
        val current = _profiles.value.toMutableList()
        val index = current.indexOfFirst { it.id == profileId }
        if (index != -1) {
            val updatedSource = current[index].officialSource.copy(
                isVerified = isVerified,
                lastVerifiedDate = verifiedDate
            )
            current[index] = current[index].copy(officialSource = updatedSource)
            _profiles.value = current
        }
    }
}
