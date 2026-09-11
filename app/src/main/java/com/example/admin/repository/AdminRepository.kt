package com.example.admin.repository

import com.example.admin.model.AdminAuditLog
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
import com.example.admin.model.VerificationStatus
import com.example.data.MockDataProvider
import com.example.model.JobCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminRepository {

    private val _jobs = MutableStateFlow<List<ManagedJob>>(emptyList())
    val jobs: StateFlow<List<ManagedJob>> = _jobs.asStateFlow()

    private val _results = MutableStateFlow<List<ManagedResult>>(emptyList())
    val results: StateFlow<List<ManagedResult>> = _results.asStateFlow()

    private val _admitCards = MutableStateFlow<List<ManagedAdmitCard>>(emptyList())
    val admitCards: StateFlow<List<ManagedAdmitCard>> = _admitCards.asStateFlow()

    private val _answerKeys = MutableStateFlow<List<ManagedAnswerKey>>(emptyList())
    val answerKeys: StateFlow<List<ManagedAnswerKey>> = _answerKeys.asStateFlow()

    private val _notifications = MutableStateFlow<List<ManagedNotification>>(emptyList())
    val notifications: StateFlow<List<ManagedNotification>> = _notifications.asStateFlow()

    private val _schemes = MutableStateFlow<List<ManagedScheme>>(emptyList())
    val schemes: StateFlow<List<ManagedScheme>> = _schemes.asStateFlow()

    private val _recruitments = MutableStateFlow<List<ManagedRecruitmentRequirement>>(emptyList())
    val recruitments: StateFlow<List<ManagedRecruitmentRequirement>> = _recruitments.asStateFlow()

    private val _appContent = MutableStateFlow<List<ManagedAppContent>>(emptyList())
    val appContent: StateFlow<List<ManagedAppContent>> = _appContent.asStateFlow()

    private val _users = MutableStateFlow<List<ManagedUserItem>>(emptyList())
    val users: StateFlow<List<ManagedUserItem>> = _users.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AdminAuditLog>>(emptyList())
    val auditLogs: StateFlow<List<AdminAuditLog>> = _auditLogs.asStateFlow()

    private val _maintenanceMode = MutableStateFlow(false)
    val maintenanceMode: StateFlow<Boolean> = _maintenanceMode.asStateFlow()

    init {
        seedInitialAdminData()
    }

    private fun seedInitialAdminData() {
        // Initial Jobs from MockDataProvider
        val initialJobs = MockDataProvider.sampleJobs.map { job ->
            ManagedJob(
                id = job.id,
                title = job.title,
                organization = job.organization,
                department = "Central / State Directorate",
                category = job.category,
                qualification = job.qualification,
                ageLimit = job.ageLimit,
                totalVacancies = job.totalVacancies,
                lastDate = job.lastDate,
                applicationFee = job.applicationFee,
                officialWebsite = "https://ssc.gov.in",
                officialNotificationUrl = "https://ssc.gov.in/notices",
                description = job.description,
                status = ContentStatus.PUBLISHED
            )
        }
        _jobs.value = initialJobs

        // Initial Results
        _results.value = listOf(
            ManagedResult(
                id = "res-ssc-cgl-tier1-2026",
                examName = "SSC CGL 2026 (Tier-I)",
                organization = "Staff Selection Commission",
                resultTitle = "Combined Graduate Level Examination (Tier-I) Scorecard & Cutoff",
                resultDate = "05 Oct 2026",
                officialResultUrl = "https://ssc.gov.in/results",
                notificationUrl = "https://ssc.gov.in/writeReadData/cgl2026_tier1.pdf",
                description = "Category-wise cutoff marks and roll numbers of 1,24,500 qualified candidates for Tier-II.",
                status = ContentStatus.PUBLISHED
            ),
            ManagedResult(
                id = "res-upsc-prelims-2026",
                examName = "UPSC Civil Services (Prelims) 2026",
                organization = "Union Public Service Commission",
                resultTitle = "Civil Services (Preliminary) Examination 2026 Qualified List",
                resultDate = "28 Sep 2026",
                officialResultUrl = "https://upsc.gov.in/examinations/results",
                description = "List of candidates shortlisted for Civil Services (Main) Examination 2026.",
                status = ContentStatus.PUBLISHED
            ),
            ManagedResult(
                id = "res-rrb-alp-cbt1-2026",
                examName = "RRB Assistant Loco Pilot (CEN 01/2026)",
                organization = "Railway Recruitment Boards",
                resultTitle = "CBT-1 Normalized Marks & Merit List for CBT-2",
                resultDate = "14 Oct 2026",
                officialResultUrl = "https://rrbapply.gov.in",
                description = "Railway recruitment shortlist for trade syllabus examination.",
                status = ContentStatus.PUBLISHED
            )
        )

        // Initial Admit Cards
        _admitCards.value = listOf(
            ManagedAdmitCard(
                id = "ac-ibps-po-mains-2026",
                examName = "IBPS PO / MT XIV Mains Exam",
                organization = "Institute of Banking Personnel Selection",
                admitCardTitle = "Online Main Examination Call Letter & Handout",
                releaseDate = "12 Oct 2026",
                examDate = "05 Nov 2026",
                officialDownloadUrl = "https://ibps.in",
                notificationUrl = "https://ibps.in/crp-po-mt-xiv",
                description = "Download call letter using Registration No and Password / DOB.",
                status = ContentStatus.PUBLISHED
            ),
            ManagedAdmitCard(
                id = "ac-ssc-chsl-tier2-2026",
                examName = "SSC CHSL (10+2) 2026 Tier-II",
                organization = "Staff Selection Commission",
                admitCardTitle = "Tier-II Descriptive & Typing Test Admission Certificate",
                releaseDate = "08 Oct 2026",
                examDate = "22 Oct 2026",
                officialDownloadUrl = "https://ssc.gov.in",
                description = "Status of city intimation slip and admission certificate for all regions.",
                status = ContentStatus.PUBLISHED
            )
        )

        // Initial Answer Keys
        _answerKeys.value = listOf(
            ManagedAnswerKey(
                id = "ak-ssc-mts-2026",
                examName = "SSC Multi Tasking Staff (MTS) 2026",
                organization = "Staff Selection Commission",
                answerKeyTitle = "Tentative Answer Keys with Candidates' Response Sheets",
                releaseDate = "10 Oct 2026",
                officialUrl = "https://ssc.gov.in/answer-keys",
                description = "Objections can be submitted online at ₹100 per question challenged.",
                status = ContentStatus.PUBLISHED
            ),
            ManagedAnswerKey(
                id = "ak-nda-2-2026",
                examName = "UPSC National Defence Academy (NDA & NA II) 2026",
                organization = "Union Public Service Commission",
                answerKeyTitle = "Official Mathematics & GAT Series-A/B/C/D Question Key",
                releaseDate = "02 Oct 2026",
                officialUrl = "https://upsc.gov.in",
                description = "Final verified answer key for entry into Army, Navy and Air Force wings.",
                status = ContentStatus.PUBLISHED
            )
        )

        // Initial Notifications
        _notifications.value = MockDataProvider.sampleNotifications.map { notif ->
            ManagedNotification(
                id = notif.id,
                title = notif.title,
                category = notif.category.name,
                description = notif.description,
                officialUrl = "https://ssc.gov.in",
                publishDate = notif.date,
                status = ContentStatus.PUBLISHED
            )
        }

        // Initial Schemes
        _schemes.value = MockDataProvider.sampleSchemes.map { s ->
            ManagedScheme(
                id = s.id,
                schemeName = s.title,
                department = s.ministry,
                eligibility = s.eligibility,
                benefits = s.benefit,
                applicationProcess = "Online portal submission with Aadhaar & DigiLocker authentication.",
                officialUrl = "https://myscheme.gov.in",
                description = "Government initiative empowering youth across all states and union territories.",
                status = ContentStatus.PUBLISHED
            )
        }

        // Initial Recruitment Requirements (Linked with Part 3)
        _recruitments.value = listOf(
            ManagedRecruitmentRequirement(
                id = "req-ssc-cgl-2026",
                recruitmentName = "SSC Combined Graduate Level (CGL)",
                organization = "Staff Selection Commission",
                category = "SSC",
                photoWidthPx = 350,
                photoHeightPx = 450,
                photoUnit = "px",
                photoMinKb = 20,
                photoMaxKb = 50,
                photoFormat = "JPG / JPEG",
                photoDpi = 200,
                photoBackground = "White or Light Gray",
                photoInstructions = "Face covering 70-80% of frame. Spectacles, caps, and masks strictly prohibited.",
                sigWidthPx = 400,
                sigHeightPx = 200,
                sigMinKb = 10,
                sigMaxKb = 20,
                sigFormat = "JPG / JPEG",
                sigBackground = "Plain white paper, black ink only",
                sigInstructions = "Sign clearly in running handwriting; BLOCK letters strictly rejected.",
                officialSourceUrl = "https://ssc.gov.in/notice_cgl2026.pdf",
                notificationNumber = "F.No. 3/1/2026-P&P-I",
                verificationStatus = VerificationStatus.VERIFIED,
                lastVerifiedDate = "10 Oct 2026",
                adminNotes = "Verified against Annexure-XI of official CGL 2026 notification."
            ),
            ManagedRecruitmentRequirement(
                id = "req-upsc-cse-2026",
                recruitmentName = "UPSC Civil Services Examination",
                organization = "Union Public Service Commission",
                category = "UPSC",
                photoWidthPx = 350,
                photoHeightPx = 450,
                photoUnit = "px",
                photoMinKb = 20,
                photoMaxKb = 300,
                photoFormat = "JPG",
                photoDpi = 300,
                photoBackground = "Plain White",
                photoInstructions = "Photo taken not more than 10 days before application start date with candidate name & date printed.",
                sigWidthPx = 350,
                sigHeightPx = 350,
                sigMinKb = 20,
                sigMaxKb = 300,
                sigFormat = "JPG",
                sigBackground = "White paper, dark black ink",
                sigInstructions = "High resolution scan without shadows.",
                officialSourceUrl = "https://upsc.gov.in/notices/cse2026.pdf",
                notificationNumber = "05/2026-CSP",
                verificationStatus = VerificationStatus.VERIFIED,
                lastVerifiedDate = "05 Oct 2026",
                adminNotes = "Updated for UPSC 10-day photo rule compliance."
            ),
            ManagedRecruitmentRequirement(
                id = "req-rrb-ntpc-2026",
                recruitmentName = "Railway NTPC & Group D",
                organization = "Railway Recruitment Boards",
                category = "Railway / RRB",
                photoWidthPx = 320,
                photoHeightPx = 400,
                photoUnit = "px",
                photoMinKb = 30,
                photoMaxKb = 70,
                photoFormat = "JPEG",
                photoDpi = 200,
                photoBackground = "White",
                photoInstructions = "Both ears clearly visible. Neutral facial expression with eyes open.",
                sigWidthPx = 400,
                sigHeightPx = 150,
                sigMinKb = 30,
                sigMaxKb = 70,
                sigFormat = "JPEG",
                sigBackground = "White paper with blue or black ballpoint pen",
                sigInstructions = "Crop tightly around signature borders.",
                officialSourceUrl = "https://rrbapply.gov.in/ntpc2026.pdf",
                notificationNumber = "CEN 03/2026",
                verificationStatus = VerificationStatus.VERIFIED,
                lastVerifiedDate = "12 Oct 2026",
                adminNotes = "Matched with Railway CEN 03/2026 Photo Guidelines."
            )
        )

        // Initial App Content
        _appContent.value = listOf(
            ManagedAppContent(
                id = "content-hero-title",
                key = "home_hero_title",
                title = "Home Hero Title",
                content = "DG with Anup — India's Premier Govt Job & Career Tools Portal",
                section = "Home Header"
            ),
            ManagedAppContent(
                id = "content-hero-subtitle",
                key = "home_hero_subtitle",
                title = "Home Hero Subtitle",
                content = "Verified government job alerts, admit cards, official results, and high-precision photo/signature makers.",
                section = "Home Header"
            ),
            ManagedAppContent(
                id = "content-disclaimer",
                key = "portal_disclaimer",
                title = "Official Information Disclaimer",
                content = "DG with Anup is a private career utility and informational dashboard. Official notifications are sourced from verified commission gazettes.",
                section = "Legal"
            ),
            ManagedAppContent(
                id = "content-privacy",
                key = "privacy_policy_text",
                title = "Privacy & Local Processing Guarantee",
                content = "All user marksheets, signatures, and photos are formatted client-side on device and never permanently stored without permission.",
                section = "Privacy"
            )
        )

        // Initial Sample Users
        _users.value = listOf(
            ManagedUserItem(
                uid = "usr-owner-001",
                name = "Anup Kumar",
                email = "blackdoremon07@gmail.com",
                phone = "+91 9876543210",
                role = "OWNER_ADMIN",
                createdAt = "01 Jan 2026",
                lastLogin = "11 Oct 2026",
                status = "ACTIVE"
            ),
            ManagedUserItem(
                uid = "usr-candidate-002",
                name = "Rahul Sharma",
                email = "rahul.ssc@gmail.com",
                phone = "+91 9811223344",
                role = "USER",
                createdAt = "15 Jan 2026",
                lastLogin = "10 Oct 2026",
                status = "ACTIVE"
            ),
            ManagedUserItem(
                uid = "usr-candidate-003",
                name = "Priya Singh",
                email = "priya.upsc@outlook.com",
                phone = "+91 9755443322",
                role = "USER",
                createdAt = "20 Feb 2026",
                lastLogin = "09 Oct 2026",
                status = "ACTIVE"
            )
        )

        // Initial Audit Logs
        _auditLogs.value = listOf(
            AdminAuditLog(
                id = "log-001",
                adminUid = "usr-owner-001",
                adminName = "Anup Kumar",
                action = "VERIFIED_RECRUITMENT_REQUIREMENT",
                collectionType = "recruitments",
                targetId = "req-ssc-cgl-2026",
                timestamp = System.currentTimeMillis() - 86400000L,
                result = "SUCCESS"
            ),
            AdminAuditLog(
                id = "log-002",
                adminUid = "usr-owner-001",
                adminName = "Anup Kumar",
                action = "PUBLISHED_JOB_NOTIFICATION",
                collectionType = "jobs",
                targetId = "job-ssc-cgl-2026",
                timestamp = System.currentTimeMillis() - 43200000L,
                result = "SUCCESS"
            )
        )
    }

    fun getStats(): AdminStats {
        return AdminStats(
            totalUsers = _users.value.size + 1238,
            totalJobs = _jobs.value.size,
            totalResults = _results.value.size,
            totalAdmitCards = _admitCards.value.size,
            totalAnswerKeys = _answerKeys.value.size,
            totalNotifications = _notifications.value.size,
            totalRecruitments = _recruitments.value.size,
            totalSchemes = _schemes.value.size,
            totalSavedItems = 3450,
            systemStatus = if (_maintenanceMode.value) "Maintenance Mode Active" else "All Services Operational"
        )
    }

    private fun logAction(adminUid: String, adminName: String, action: String, collectionType: String, targetId: String) {
        val newLog = AdminAuditLog(
            id = "log_${System.currentTimeMillis()}",
            adminUid = adminUid,
            adminName = adminName,
            action = action,
            collectionType = collectionType,
            targetId = targetId,
            timestamp = System.currentTimeMillis(),
            result = "SUCCESS"
        )
        _auditLogs.value = listOf(newLog) + _auditLogs.value
    }

    // =====================================
    // Job CRUD
    // =====================================

    fun saveJob(job: ManagedJob, adminUid: String, adminName: String) {
        val current = _jobs.value.toMutableList()
        val index = current.indexOfFirst { it.id == job.id }
        if (index >= 0) {
            current[index] = job
            logAction(adminUid, adminName, "UPDATED_JOB", "jobs", job.id)
        } else {
            current.add(0, job)
            logAction(adminUid, adminName, "CREATED_JOB", "jobs", job.id)
        }
        _jobs.value = current
    }

    fun deleteJob(jobId: String, adminUid: String, adminName: String) {
        _jobs.value = _jobs.value.filterNot { it.id == jobId }
        logAction(adminUid, adminName, "DELETED_JOB", "jobs", jobId)
    }

    // =====================================
    // Result CRUD
    // =====================================

    fun saveResult(result: ManagedResult, adminUid: String, adminName: String) {
        val current = _results.value.toMutableList()
        val index = current.indexOfFirst { it.id == result.id }
        if (index >= 0) {
            current[index] = result
            logAction(adminUid, adminName, "UPDATED_RESULT", "results", result.id)
        } else {
            current.add(0, result)
            logAction(adminUid, adminName, "CREATED_RESULT", "results", result.id)
        }
        _results.value = current
    }

    fun deleteResult(resultId: String, adminUid: String, adminName: String) {
        _results.value = _results.value.filterNot { it.id == resultId }
        logAction(adminUid, adminName, "DELETED_RESULT", "results", resultId)
    }

    // =====================================
    // Admit Card CRUD
    // =====================================

    fun saveAdmitCard(card: ManagedAdmitCard, adminUid: String, adminName: String) {
        val current = _admitCards.value.toMutableList()
        val index = current.indexOfFirst { it.id == card.id }
        if (index >= 0) {
            current[index] = card
            logAction(adminUid, adminName, "UPDATED_ADMIT_CARD", "admit_cards", card.id)
        } else {
            current.add(0, card)
            logAction(adminUid, adminName, "CREATED_ADMIT_CARD", "admit_cards", card.id)
        }
        _admitCards.value = current
    }

    fun deleteAdmitCard(cardId: String, adminUid: String, adminName: String) {
        _admitCards.value = _admitCards.value.filterNot { it.id == cardId }
        logAction(adminUid, adminName, "DELETED_ADMIT_CARD", "admit_cards", cardId)
    }

    // =====================================
    // Answer Key CRUD
    // =====================================

    fun saveAnswerKey(key: ManagedAnswerKey, adminUid: String, adminName: String) {
        val current = _answerKeys.value.toMutableList()
        val index = current.indexOfFirst { it.id == key.id }
        if (index >= 0) {
            current[index] = key
            logAction(adminUid, adminName, "UPDATED_ANSWER_KEY", "answer_keys", key.id)
        } else {
            current.add(0, key)
            logAction(adminUid, adminName, "CREATED_ANSWER_KEY", "answer_keys", key.id)
        }
        _answerKeys.value = current
    }

    fun deleteAnswerKey(keyId: String, adminUid: String, adminName: String) {
        _answerKeys.value = _answerKeys.value.filterNot { it.id == keyId }
        logAction(adminUid, adminName, "DELETED_ANSWER_KEY", "answer_keys", keyId)
    }

    // =====================================
    // Notification CRUD
    // =====================================

    fun saveNotification(notif: ManagedNotification, adminUid: String, adminName: String) {
        val current = _notifications.value.toMutableList()
        val index = current.indexOfFirst { it.id == notif.id }
        if (index >= 0) {
            current[index] = notif
            logAction(adminUid, adminName, "UPDATED_NOTIFICATION", "notifications", notif.id)
        } else {
            current.add(0, notif)
            logAction(adminUid, adminName, "CREATED_NOTIFICATION", "notifications", notif.id)
        }
        _notifications.value = current
    }

    fun deleteNotification(notifId: String, adminUid: String, adminName: String) {
        _notifications.value = _notifications.value.filterNot { it.id == notifId }
        logAction(adminUid, adminName, "DELETED_NOTIFICATION", "notifications", notifId)
    }

    // =====================================
    // Scheme CRUD
    // =====================================

    fun saveScheme(scheme: ManagedScheme, adminUid: String, adminName: String) {
        val current = _schemes.value.toMutableList()
        val index = current.indexOfFirst { it.id == scheme.id }
        if (index >= 0) {
            current[index] = scheme
            logAction(adminUid, adminName, "UPDATED_SCHEME", "schemes", scheme.id)
        } else {
            current.add(0, scheme)
            logAction(adminUid, adminName, "CREATED_SCHEME", "schemes", scheme.id)
        }
        _schemes.value = current
    }

    fun deleteScheme(schemeId: String, adminUid: String, adminName: String) {
        _schemes.value = _schemes.value.filterNot { it.id == schemeId }
        logAction(adminUid, adminName, "DELETED_SCHEME", "schemes", schemeId)
    }

    // =====================================
    // Recruitment Requirements Manager CRUD
    // =====================================

    fun saveRecruitment(req: ManagedRecruitmentRequirement, adminUid: String, adminName: String) {
        val current = _recruitments.value.toMutableList()
        val index = current.indexOfFirst { it.id == req.id }
        if (index >= 0) {
            current[index] = req
            logAction(adminUid, adminName, "UPDATED_RECRUITMENT_REQUIREMENT", "recruitments", req.id)
        } else {
            current.add(0, req)
            logAction(adminUid, adminName, "CREATED_RECRUITMENT_REQUIREMENT", "recruitments", req.id)
        }
        _recruitments.value = current
    }

    fun deleteRecruitment(reqId: String, adminUid: String, adminName: String) {
        _recruitments.value = _recruitments.value.filterNot { it.id == reqId }
        logAction(adminUid, adminName, "DELETED_RECRUITMENT_REQUIREMENT", "recruitments", reqId)
    }

    // =====================================
    // App Content CRUD
    // =====================================

    fun saveAppContent(content: ManagedAppContent, adminUid: String, adminName: String) {
        val current = _appContent.value.toMutableList()
        val index = current.indexOfFirst { it.id == content.id }
        if (index >= 0) {
            current[index] = content
            logAction(adminUid, adminName, "UPDATED_APP_CONTENT", "app_content", content.key)
        } else {
            current.add(0, content)
            logAction(adminUid, adminName, "CREATED_APP_CONTENT", "app_content", content.key)
        }
        _appContent.value = current
    }

    // =====================================
    // User Management
    // =====================================

    fun toggleUserStatus(uid: String, adminUid: String, adminName: String) {
        val current = _users.value.toMutableList()
        val index = current.indexOfFirst { it.uid == uid }
        if (index >= 0) {
            val user = current[index]
            val newStatus = if (user.status == "ACTIVE") "DISABLED" else "ACTIVE"
            current[index] = user.copy(status = newStatus)
            _users.value = current
            logAction(adminUid, adminName, "TOGGLED_USER_STATUS_$newStatus", "users", uid)
        }
    }

    fun setMaintenanceMode(enabled: Boolean, adminUid: String, adminName: String) {
        _maintenanceMode.value = enabled
        logAction(adminUid, adminName, if (enabled) "ENABLED_MAINTENANCE_MODE" else "DISABLED_MAINTENANCE_MODE", "settings", "maintenance")
    }

    fun clearTemporaryCache(adminUid: String, adminName: String) {
        logAction(adminUid, adminName, "CLEARED_TEMP_CACHE", "system", "cache")
    }
}
