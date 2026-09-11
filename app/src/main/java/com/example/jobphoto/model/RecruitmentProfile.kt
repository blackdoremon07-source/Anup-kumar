package com.example.jobphoto.model

data class PhotoRequirement(
    val width: Int,
    val height: Int,
    val unit: DimensionUnit = DimensionUnit.PX,
    val displayDimensionString: String = "${width} × ${height} px",
    val aspectRatio: String = "3.5 : 4.5",
    val minKb: Int = 20,
    val maxKb: Int = 50,
    val requiredFormat: String = "JPG / JPEG",
    val dpi: Int? = 200,
    val backgroundRequirement: String = "Plain White or Off-White background",
    val colourRequirement: String = "Color photo, 80% face coverage",
    val otherInstructions: String = "Both ears must be visible, neutral facial expression, no spectacles or cap."
)

data class SignatureRequirement(
    val width: Int,
    val height: Int,
    val unit: DimensionUnit = DimensionUnit.PX,
    val displayDimensionString: String = "${width} × ${height} px",
    val aspectRatio: String = "2.0 : 1.0",
    val minKb: Int = 10,
    val maxKb: Int = 20,
    val requiredFormat: String = "JPG / JPEG",
    val dpi: Int? = 200,
    val background: String = "Clear white paper, black or blue ink",
    val otherInstructions: String = "Running handwriting only. Signatures in ALL CAPITAL letters are strictly rejected."
)

data class DocumentRequirement(
    val requiredFormat: String = "PDF",
    val maxKb: Int = 300,
    val maxPages: Int = 2,
    val numberOfFiles: Int = 1,
    val otherInstructions: String = "Clear legible scan of original educational certificate / caste certificate without watermarks."
)

data class OfficialSource(
    val sourceName: String,
    val sourceUrl: String,
    val notificationTitleOrNumber: String,
    val isVerified: Boolean = true,
    val lastVerifiedDate: String = "August 2024",
    val unverifiedNotice: String = "Official requirement not verified yet. Do not present guessed values as official."
)

/**
 * Reusable data structure for Government Job Photo & Signature profiles.
 * Fully compatible with Part 6 Admin Panel for adding, editing, and deleting.
 */
data class RecruitmentProfile(
    val id: String,
    val name: String,
    val organization: String,
    val category: RecruitmentCategory,
    val photoRequirement: PhotoRequirement,
    val signatureRequirement: SignatureRequirement,
    val documentRequirement: DocumentRequirement,
    val officialSource: OfficialSource,
    val isPopular: Boolean = false,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
