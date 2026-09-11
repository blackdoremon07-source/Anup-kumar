package com.example.admin.model

import com.example.model.JobCategory

enum class ContentStatus {
    DRAFT,
    PUBLISHED,
    CLOSED
}

enum class VerificationStatus(val label: String, val colorHex: Long) {
    VERIFIED("Verified Official", 0xFF16A34A),
    UNVERIFIED("Unverified", 0xFFEAB308),
    NEEDS_REVIEW("Needs Review", 0xFFEA580C)
}

data class AdminStats(
    val totalUsers: Int = 1240,
    val totalJobs: Int = 18,
    val totalResults: Int = 14,
    val totalAdmitCards: Int = 9,
    val totalAnswerKeys: Int = 7,
    val totalNotifications: Int = 26,
    val totalRecruitments: Int = 12,
    val totalSchemes: Int = 8,
    val totalSavedItems: Int = 3450,
    val systemStatus: String = "Operational • All Services Active"
)

data class AdminAuditLog(
    val id: String,
    val adminUid: String,
    val adminName: String,
    val action: String,
    val collectionType: String,
    val targetId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val result: String = "SUCCESS"
)

data class ManagedJob(
    val id: String,
    val title: String,
    val organization: String,
    val department: String = "",
    val category: JobCategory,
    val qualification: String,
    val ageLimit: String,
    val totalVacancies: String,
    val applicationStartDate: String = "01 Oct 2026",
    val lastDate: String,
    val examDate: String = "Dec 2026",
    val selectionProcess: String = "Written Exam + Document Verification",
    val salary: String = "₹35,400 - ₹1,12,400 (Level 6)",
    val importantDates: String = "Apply: Ongoing • Exam: Scheduled",
    val applicationFee: String,
    val officialWebsite: String = "https://ssc.gov.in",
    val officialNotificationUrl: String = "https://ssc.gov.in/notices",
    val officialApplicationUrl: String = "https://ssc.gov.in/apply",
    val officialSourceUrl: String = "https://ssc.gov.in",
    val lastVerifiedDate: String = "15 Oct 2026",
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val description: String,
    val status: ContentStatus = ContentStatus.PUBLISHED,
    val publishedDate: String = "15 Oct 2026"
)

data class ManagedResult(
    val id: String,
    val examName: String,
    val organization: String,
    val resultTitle: String,
    val resultDate: String,
    val officialResultUrl: String,
    val notificationUrl: String = "",
    val officialSourceUrl: String = "https://ssc.gov.in",
    val lastVerifiedDate: String = "15 Oct 2026",
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val description: String = "",
    val status: ContentStatus = ContentStatus.PUBLISHED
)

data class ManagedAdmitCard(
    val id: String,
    val examName: String,
    val organization: String,
    val admitCardTitle: String,
    val releaseDate: String,
    val examDate: String,
    val officialDownloadUrl: String,
    val notificationUrl: String = "",
    val officialSourceUrl: String = "https://ssc.gov.in",
    val lastVerifiedDate: String = "15 Oct 2026",
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val description: String = "",
    val status: ContentStatus = ContentStatus.PUBLISHED
)

data class ManagedAnswerKey(
    val id: String,
    val examName: String,
    val organization: String,
    val answerKeyTitle: String,
    val releaseDate: String,
    val officialUrl: String,
    val notificationUrl: String = "",
    val officialSourceUrl: String = "https://ssc.gov.in",
    val lastVerifiedDate: String = "15 Oct 2026",
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val description: String = "",
    val status: ContentStatus = ContentStatus.PUBLISHED
)

data class ManagedNotification(
    val id: String,
    val title: String,
    val category: String, // Important, Exam, Application, Government, Recruitment, Other
    val organization: String = "Central Recruitment Commission",
    val description: String,
    val officialUrl: String,
    val notificationUrl: String = "",
    val officialSourceUrl: String = "https://india.gov.in",
    val lastVerifiedDate: String = "15 Oct 2026",
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val publishDate: String,
    val status: ContentStatus = ContentStatus.PUBLISHED
)

data class ManagedScheme(
    val id: String,
    val schemeName: String,
    val department: String,
    val eligibility: String,
    val benefits: String,
    val applicationProcess: String,
    val officialUrl: String,
    val officialSourceUrl: String = "https://myscheme.gov.in",
    val lastVerifiedDate: String = "15 Oct 2026",
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val description: String,
    val status: ContentStatus = ContentStatus.PUBLISHED
)

data class ManagedRecruitmentRequirement(
    val id: String,
    val recruitmentName: String,
    val organization: String,
    val category: String,
    // Photo specs
    val photoWidthPx: Int = 350,
    val photoHeightPx: Int = 450,
    val photoUnit: String = "px",
    val photoMinKb: Int = 20,
    val photoMaxKb: Int = 50,
    val photoFormat: String = "JPG / JPEG",
    val photoDpi: Int = 200,
    val photoBackground: String = "White / Light Plain",
    val photoInstructions: String = "Recent passport photo with name & date printed at bottom.",
    // Signature specs
    val sigWidthPx: Int = 400,
    val sigHeightPx: Int = 200,
    val sigUnit: String = "px",
    val sigMinKb: Int = 10,
    val sigMaxKb: Int = 20,
    val sigFormat: String = "JPG / JPEG",
    val sigDpi: Int = 200,
    val sigBackground: String = "Plain White Paper with Black Ink",
    val sigInstructions: String = "Sign with running hand in black ballpoint pen only.",
    // Document specs
    val docFormat: String = "PDF",
    val docMaxKb: Int = 500,
    val docMaxPages: Int = 2,
    val docInstructions: String = "Clear color scan of original certificate.",
    // Verification & Official Source
    val officialSourceUrl: String = "",
    val notificationNumber: String = "",
    val sourceType: String = "Official Gazette / Commission Portal",
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val lastVerifiedDate: String = "10 Oct 2026",
    val adminNotes: String = "Verified against official notification PDF."
)

data class ManagedAppContent(
    val id: String,
    val key: String,
    val title: String,
    val content: String,
    val section: String = "General",
    val lastUpdated: Long = System.currentTimeMillis()
)

data class ManagedUserItem(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val createdAt: String,
    val lastLogin: String,
    val status: String = "ACTIVE"
)

enum class AdminSection(val title: String, val iconName: String) {
    DASHBOARD("Dashboard", "Dashboard"),
    USERS("Users", "People"),
    JOBS("Jobs", "Work"),
    RESULTS("Results", "Assessment"),
    ADMIT_CARDS("Admit Cards", "Badge"),
    ANSWER_KEYS("Answer Keys", "Key"),
    NOTIFICATIONS("Notifications", "Notifications"),
    SCHEMES("Schemes", "AccountBalance"),
    RECRUITMENTS("Recruitment Specs", "FactCheck"),
    APP_CONTENT("App Content", "Article"),
    SETTINGS("Settings", "Settings"),
    AUDIT_LOG("Audit Log", "Security")
}
