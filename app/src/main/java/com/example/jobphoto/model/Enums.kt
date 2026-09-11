package com.example.jobphoto.model

enum class RecruitmentCategory(val displayName: String) {
    ALL("All Categories"),
    SSC("SSC"),
    UPSC("UPSC"),
    RAILWAY("Railway / RRB"),
    BANKING("Banking"),
    POLICE("Police"),
    DEFENCE("Defence"),
    TEACHING("Teaching"),
    STATE_GOVT("State Government"),
    CENTRAL_GOVT("Central Government"),
    OTHER("Other")
}

enum class DimensionUnit(val symbol: String) {
    PX("px"),
    MM("mm"),
    CM("cm")
}

enum class ValidationCheckStatus {
    PASS,
    WARNING,
    FAIL
}

data class ValidationRuleResult(
    val title: String,
    val detail: String,
    val status: ValidationCheckStatus
)

data class JobPhotoValidationReport(
    val photoDimensions: ValidationRuleResult,
    val photoAspectRatio: ValidationRuleResult,
    val photoFileSize: ValidationRuleResult,
    val photoFormat: ValidationRuleResult,
    val photoBackground: ValidationRuleResult,
    val photoDpi: ValidationRuleResult,
    val signatureDimensions: ValidationRuleResult,
    val signatureSize: ValidationRuleResult,
    val signatureFormat: ValidationRuleResult
) {
    val isFullyCompliant: Boolean
        get() = listOf(
            photoDimensions,
            photoAspectRatio,
            photoFileSize,
            photoFormat,
            photoDpi,
            signatureDimensions,
            signatureSize,
            signatureFormat
        ).none { it.status == ValidationCheckStatus.FAIL }

    val hasWarnings: Boolean
        get() = listOf(
            photoDimensions,
            photoAspectRatio,
            photoFileSize,
            photoFormat,
            photoBackground,
            photoDpi,
            signatureDimensions,
            signatureSize,
            signatureFormat
        ).any { it.status == ValidationCheckStatus.WARNING }
}
