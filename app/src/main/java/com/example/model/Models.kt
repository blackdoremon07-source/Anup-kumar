package com.example.model

/**
 * Clean data architecture for "DG with Anup"
 * Designed for seamless Room / Firebase backend database replacement in later parts (Part 5 & 7).
 */

data class User(
    val id: String = "",
    val name: String = "Student Aspirant",
    val email: String = "",
    val phone: String = "",
    val avatarUrl: String? = null,
    val savedJobIds: List<String> = emptyList(),
    val downloadedItemIds: List<String> = emptyList(),
    val isGuest: Boolean = true,
    val registeredAtMillis: Long = System.currentTimeMillis()
)

enum class JobCategory(val displayName: String) {
    ALL("All"),
    SSC("SSC"),
    UPSC("UPSC"),
    RAILWAY("Railway / RRB"),
    BANKING("Banking"),
    POLICE("Police"),
    DEFENCE("Defence"),
    TEACHING("Teaching"),
    STATE_GOVT("State Govt"),
    CENTRAL_GOVT("Central Govt"),
    OTHER("Other Government Jobs")
}

data class Job(
    val id: String,
    val title: String,
    val organization: String,
    val category: JobCategory,
    val lastDate: String,
    val qualification: String,
    val totalVacancies: String,
    val salary: String,
    val location: String,
    val status: String, // e.g., "Active", "Ending Soon", "New"
    val applyUrl: String = "",
    val notificationUrl: String = "",
    val ageLimit: String = "",
    val applicationFee: String = "",
    val description: String = ""
)

data class Result(
    val id: String,
    val title: String,
    val organization: String,
    val category: JobCategory,
    val declaredDate: String,
    val examDate: String,
    val downloadUrl: String = "",
    val status: String = "Declared"
)

data class AdmitCard(
    val id: String,
    val title: String,
    val organization: String,
    val category: JobCategory,
    val examDate: String,
    val releaseDate: String,
    val downloadUrl: String = "",
    val status: String = "Available"
)

data class AnswerKey(
    val id: String,
    val title: String,
    val organization: String,
    val category: JobCategory,
    val examDate: String,
    val objectionLastDate: String,
    val downloadUrl: String = "",
    val status: String = "Out Now"
)

data class Notification(
    val id: String,
    val title: String,
    val organization: String,
    val date: String,
    val category: JobCategory,
    val importance: NotificationImportance = NotificationImportance.NORMAL,
    val description: String,
    val pdfUrl: String = ""
)

enum class NotificationImportance {
    URGENT, IMPORTANT, NORMAL
}

data class GovernmentScheme(
    val id: String,
    val title: String,
    val ministry: String,
    val benefit: String,
    val eligibility: String,
    val deadline: String,
    val category: String,
    val applyUrl: String = ""
)

data class Tool(
    val id: String,
    val title: String,
    val description: String,
    val route: String,
    val iconName: String,
    val category: String,
    val badgeText: String? = null,
    val partNotice: String? = null
)

data class RecruitmentRequirement(
    val id: String,
    val jobId: String,
    val photoWidthPx: Int = 350,
    val photoHeightPx: Int = 450,
    val photoMaxSizeBytes: Int = 50 * 1024,
    val photoMinSizeBytes: Int = 20 * 1024,
    val signatureWidthPx: Int = 280,
    val signatureHeightPx: Int = 120,
    val signatureMaxSizeBytes: Int = 20 * 1024,
    val signatureMinSizeBytes: Int = 10 * 1024,
    val allowedFormats: List<String> = listOf("JPG", "JPEG"),
    val dateOnPhotoRequired: Boolean = false,
    val nameOnPhotoRequired: Boolean = false
)
