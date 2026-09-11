package com.example.auth.model

enum class UserRole(val label: String, val badgeColorHex: Long) {
    USER("Candidate User", 0xFF2563EB),
    OWNER_ADMIN("Owner Administrator", 0xFFDC2626)
}

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String? = null,
    val photoURL: String? = null,
    val role: UserRole = UserRole.USER,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val accountStatus: String = "ACTIVE", // ACTIVE or DISABLED
    val savedItemIds: List<String> = emptyList(),
    val downloadHistory: List<String> = emptyList()
) {
    val isOwnerAdmin: Boolean
        get() = role == UserRole.OWNER_ADMIN
}

sealed class AuthStatus {
    object Idle : AuthStatus()
    object Loading : AuthStatus()
    data class Authenticated(val user: UserProfile) : AuthStatus()
    object Unauthenticated : AuthStatus()
    data class Error(val message: String) : AuthStatus()
}

enum class LoginMethod {
    GOOGLE,
    PHONE_OTP,
    EMAIL_PASSWORD
}

data class PhoneOtpState(
    val phoneNumber: String = "",
    val verificationId: String? = null,
    val otpCode: String = "",
    val isOtpSent: Boolean = false,
    val resendCooldownSeconds: Int = 0,
    val remainingAttempts: Int = 3,
    val error: String? = null
)
