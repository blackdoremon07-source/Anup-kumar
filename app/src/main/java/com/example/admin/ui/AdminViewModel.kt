package com.example.admin.ui

import androidx.lifecycle.ViewModel
import com.example.admin.model.AdminAuditLog
import com.example.admin.model.AdminSection
import com.example.admin.model.AdminStats
import com.example.admin.model.ContentStatus
import com.example.admin.model.ManagedAdmitCard
import com.example.admin.model.ManagedAnswerKey
import com.example.admin.model.ManagedAppContent
import com.example.admin.model.ManagedJob
import com.example.admin.model.ManagedNotification
import com.example.admin.model.ManagedRecruitmentRequirement
import com.example.admin.model.ManagedResult
import com.example.admin.model.ManagedScheme
import com.example.admin.model.ManagedUserItem
import com.example.admin.repository.AdminRepository
import com.example.auth.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminViewModel(
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {

    private val _currentSection = MutableStateFlow(AdminSection.DASHBOARD)
    val currentSection: StateFlow<AdminSection> = _currentSection.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow<ContentStatus?>(null)
    val statusFilter: StateFlow<ContentStatus?> = _statusFilter.asStateFlow()

    val jobs: StateFlow<List<ManagedJob>> = repository.jobs
    val results: StateFlow<List<ManagedResult>> = repository.results
    val admitCards: StateFlow<List<ManagedAdmitCard>> = repository.admitCards
    val answerKeys: StateFlow<List<ManagedAnswerKey>> = repository.answerKeys
    val notifications: StateFlow<List<ManagedNotification>> = repository.notifications
    val schemes: StateFlow<List<ManagedScheme>> = repository.schemes
    val recruitments: StateFlow<List<ManagedRecruitmentRequirement>> = repository.recruitments
    val appContent: StateFlow<List<ManagedAppContent>> = repository.appContent
    val users: StateFlow<List<ManagedUserItem>> = repository.users
    val auditLogs: StateFlow<List<AdminAuditLog>> = repository.auditLogs
    val maintenanceMode: StateFlow<Boolean> = repository.maintenanceMode

    private val _notificationBanner = MutableStateFlow<String?>(null)
    val notificationBanner: StateFlow<String?> = _notificationBanner.asStateFlow()

    // Dialog state for Job Edit/Add
    private val _jobDialogItem = MutableStateFlow<ManagedJob?>(null)
    val jobDialogItem: StateFlow<ManagedJob?> = _jobDialogItem.asStateFlow()

    // Dialog state for Result Edit/Add
    private val _resultDialogItem = MutableStateFlow<ManagedResult?>(null)
    val resultDialogItem: StateFlow<ManagedResult?> = _resultDialogItem.asStateFlow()

    // Dialog state for Admit Card Edit/Add
    private val _admitCardDialogItem = MutableStateFlow<ManagedAdmitCard?>(null)
    val admitCardDialogItem: StateFlow<ManagedAdmitCard?> = _admitCardDialogItem.asStateFlow()

    // Dialog state for Recruitment Requirement Edit/Add
    private val _recruitmentDialogItem = MutableStateFlow<ManagedRecruitmentRequirement?>(null)
    val recruitmentDialogItem: StateFlow<ManagedRecruitmentRequirement?> = _recruitmentDialogItem.asStateFlow()

    // Generic Delete Confirmation Dialog: (title, message, onConfirm action)
    private val _deleteConfirmation = MutableStateFlow<Pair<String, () -> Unit>?>(null)
    val deleteConfirmation: StateFlow<Pair<String, () -> Unit>?> = _deleteConfirmation.asStateFlow()

    fun selectSection(section: AdminSection) {
        _currentSection.value = section
        _searchQuery.value = ""
        _statusFilter.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: ContentStatus?) {
        _statusFilter.value = status
    }

    fun clearBanner() {
        _notificationBanner.value = null
    }

    fun getStats(): AdminStats = repository.getStats()

    // =====================================
    // Job Management
    // =====================================

    fun openJobDialog(job: ManagedJob?) {
        _jobDialogItem.value = job
    }

    fun closeJobDialog() {
        _jobDialogItem.value = null
    }

    fun saveJob(job: ManagedJob, user: UserProfile) {
        repository.saveJob(job, user.uid, user.name)
        _notificationBanner.value = "Job '${job.title}' saved successfully."
        closeJobDialog()
    }

    fun confirmDeleteJob(job: ManagedJob, user: UserProfile) {
        _deleteConfirmation.value = Pair("Are you sure you want to delete job '${job.title}'?") {
            repository.deleteJob(job.id, user.uid, user.name)
            _notificationBanner.value = "Job deleted."
            _deleteConfirmation.value = null
        }
    }

    // =====================================
    // Result Management
    // =====================================

    fun openResultDialog(result: ManagedResult?) {
        _resultDialogItem.value = result
    }

    fun closeResultDialog() {
        _resultDialogItem.value = null
    }

    fun saveResult(result: ManagedResult, user: UserProfile) {
        repository.saveResult(result, user.uid, user.name)
        _notificationBanner.value = "Result '${result.resultTitle}' saved successfully."
        closeResultDialog()
    }

    fun confirmDeleteResult(result: ManagedResult, user: UserProfile) {
        _deleteConfirmation.value = Pair("Are you sure you want to delete result '${result.resultTitle}'?") {
            repository.deleteResult(result.id, user.uid, user.name)
            _notificationBanner.value = "Result deleted."
            _deleteConfirmation.value = null
        }
    }

    // =====================================
    // Admit Card Management
    // =====================================

    fun openAdmitCardDialog(card: ManagedAdmitCard?) {
        _admitCardDialogItem.value = card
    }

    fun closeAdmitCardDialog() {
        _admitCardDialogItem.value = null
    }

    fun saveAdmitCard(card: ManagedAdmitCard, user: UserProfile) {
        repository.saveAdmitCard(card, user.uid, user.name)
        _notificationBanner.value = "Admit Card '${card.admitCardTitle}' saved."
        closeAdmitCardDialog()
    }

    fun confirmDeleteAdmitCard(card: ManagedAdmitCard, user: UserProfile) {
        _deleteConfirmation.value = Pair("Are you sure you want to delete admit card '${card.admitCardTitle}'?") {
            repository.deleteAdmitCard(card.id, user.uid, user.name)
            _notificationBanner.value = "Admit card deleted."
            _deleteConfirmation.value = null
        }
    }

    // =====================================
    // Recruitment Requirements Management
    // =====================================

    fun openRecruitmentDialog(req: ManagedRecruitmentRequirement?) {
        _recruitmentDialogItem.value = req
    }

    fun closeRecruitmentDialog() {
        _recruitmentDialogItem.value = null
    }

    fun saveRecruitment(req: ManagedRecruitmentRequirement, user: UserProfile) {
        repository.saveRecruitment(req, user.uid, user.name)
        _notificationBanner.value = "Recruitment specs for '${req.recruitmentName}' saved."
        closeRecruitmentDialog()
    }

    fun confirmDeleteRecruitment(req: ManagedRecruitmentRequirement, user: UserProfile) {
        _deleteConfirmation.value = Pair("Are you sure you want to delete recruitment profile '${req.recruitmentName}'?") {
            repository.deleteRecruitment(req.id, user.uid, user.name)
            _notificationBanner.value = "Recruitment profile removed."
            _deleteConfirmation.value = null
        }
    }

    // =====================================
    // User Status & Settings
    // =====================================

    fun toggleUserStatus(uid: String, user: UserProfile) {
        repository.toggleUserStatus(uid, user.uid, user.name)
        _notificationBanner.value = "User status updated."
    }

    fun toggleMaintenanceMode(user: UserProfile) {
        val next = !repository.maintenanceMode.value
        repository.setMaintenanceMode(next, user.uid, user.name)
        _notificationBanner.value = if (next) "Maintenance Mode enabled" else "Maintenance Mode disabled"
    }

    fun clearTempCache(user: UserProfile) {
        repository.clearTemporaryCache(user.uid, user.name)
        _notificationBanner.value = "Temporary cache cleared."
    }

    fun dismissDeleteConfirmation() {
        _deleteConfirmation.value = null
    }
}
