package com.example.auth.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.model.AuthStatus
import com.example.auth.model.PhoneOtpState
import com.example.auth.model.UserProfile
import com.example.auth.model.UserRole
import com.example.auth.repository.AuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    val repository = AuthRepository(application.applicationContext)

    val currentUser: StateFlow<UserProfile?> = repository.currentUser

    private val _authStatus = MutableStateFlow<AuthStatus>(AuthStatus.Idle)
    val authStatus: StateFlow<AuthStatus> = _authStatus.asStateFlow()

    private val _phoneOtpState = MutableStateFlow(PhoneOtpState())
    val phoneOtpState: StateFlow<PhoneOtpState> = _phoneOtpState.asStateFlow()

    private val _isSignUpMode = MutableStateFlow(false)
    val isSignUpMode: StateFlow<Boolean> = _isSignUpMode.asStateFlow()

    private val _forgotPasswordOpen = MutableStateFlow(false)
    val forgotPasswordOpen: StateFlow<Boolean> = _forgotPasswordOpen.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private var cooldownJob: Job? = null

    init {
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                if (user != null) {
                    _authStatus.value = AuthStatus.Authenticated(user)
                } else {
                    _authStatus.value = AuthStatus.Unauthenticated
                }
            }
        }
    }

    fun setSignUpMode(isSignUp: Boolean) {
        _isSignUpMode.value = isSignUp
        _statusMessage.value = null
    }

    fun setForgotPasswordOpen(isOpen: Boolean) {
        _forgotPasswordOpen.value = isOpen
        _statusMessage.value = null
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    // =====================================
    // Email Authentication
    // =====================================

    fun submitEmailAuth(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading
            _statusMessage.value = null
            if (_isSignUpMode.value) {
                val result = repository.signUpWithEmail(name, email, pass)
                result.fold(
                    onSuccess = { user ->
                        _authStatus.value = AuthStatus.Authenticated(user)
                        _statusMessage.value = "Account created successfully! Welcome, ${user.name}."
                    },
                    onFailure = { err ->
                        _authStatus.value = AuthStatus.Error(err.message ?: "Sign up failed")
                        _statusMessage.value = err.message
                    }
                )
            } else {
                val result = repository.signInWithEmail(email, pass)
                result.fold(
                    onSuccess = { user ->
                        _authStatus.value = AuthStatus.Authenticated(user)
                        _statusMessage.value = "Welcome back, ${user.name}!"
                    },
                    onFailure = { err ->
                        _authStatus.value = AuthStatus.Error(err.message ?: "Login failed")
                        _statusMessage.value = err.message
                    }
                )
            }
        }
    }

    fun requestPasswordReset(email: String) {
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading
            val result = repository.sendPasswordReset(email)
            result.fold(
                onSuccess = { msg ->
                    _forgotPasswordOpen.value = false
                    _statusMessage.value = msg
                    _authStatus.value = AuthStatus.Unauthenticated
                },
                onFailure = { err ->
                    _statusMessage.value = err.message
                    _authStatus.value = AuthStatus.Unauthenticated
                }
            )
        }
    }

    // =====================================
    // Mobile OTP Authentication
    // =====================================

    fun sendPhoneOtp(phoneNumber: String) {
        viewModelScope.launch {
            val res = repository.requestPhoneOtp(phoneNumber)
            res.fold(
                onSuccess = { cooldown ->
                    _phoneOtpState.value = _phoneOtpState.value.copy(
                        phoneNumber = phoneNumber,
                        isOtpSent = true,
                        resendCooldownSeconds = cooldown,
                        remainingAttempts = 3,
                        error = null
                    )
                    _statusMessage.value = "OTP sent to +91 $phoneNumber. (Demo OTP: 123456)"
                    startCooldownTimer(cooldown)
                },
                onFailure = { err ->
                    _phoneOtpState.value = _phoneOtpState.value.copy(error = err.message)
                    _statusMessage.value = err.message
                }
            )
        }
    }

    private fun startCooldownTimer(seconds: Int) {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            var currentSec = seconds
            while (currentSec > 0) {
                delay(1000L)
                currentSec--
                _phoneOtpState.value = _phoneOtpState.value.copy(resendCooldownSeconds = currentSec)
            }
        }
    }

    fun verifyPhoneOtp(otpCode: String) {
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading
            val phone = _phoneOtpState.value.phoneNumber
            val result = repository.verifyPhoneOtp(phone, otpCode)
            result.fold(
                onSuccess = { user ->
                    _authStatus.value = AuthStatus.Authenticated(user)
                    _phoneOtpState.value = PhoneOtpState() // reset
                    _statusMessage.value = "Mobile verified successfully! Welcome, ${user.name}."
                },
                onFailure = { err ->
                    _authStatus.value = AuthStatus.Unauthenticated
                    _phoneOtpState.value = _phoneOtpState.value.copy(
                        error = err.message,
                        remainingAttempts = _phoneOtpState.value.remainingAttempts - 1
                    )
                    _statusMessage.value = err.message
                }
            )
        }
    }

    // =====================================
    // Google Sign-In
    // =====================================

    fun signInWithGoogle(idToken: String, displayName: String?, email: String?, photoUrl: String?) {
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading
            val result = repository.signInWithGoogle(idToken, displayName, email, photoUrl)
            result.fold(
                onSuccess = { user ->
                    _authStatus.value = AuthStatus.Authenticated(user)
                    _statusMessage.value = "Signed in with Google as ${user.name}."
                },
                onFailure = { err ->
                    _authStatus.value = AuthStatus.Error(err.message ?: "Google Sign-In failed")
                    _statusMessage.value = err.message
                }
            )
        }
    }

    // =====================================
    // Profile & Role Management
    // =====================================

    fun updateProfile(name: String, phone: String?) {
        val res = repository.updateProfile(name, phone)
        res.fold(
            onSuccess = { _statusMessage.value = "Profile updated successfully!" },
            onFailure = { _statusMessage.value = it.message }
        )
    }

    fun toggleSavedItem(itemId: String) {
        val isSaved = repository.toggleSavedItem(itemId)
        _statusMessage.value = if (isSaved) "Item saved to your profile" else "Item removed from saved list"
    }

    fun toggleOwnerAdminTestRole(isAdmin: Boolean) {
        repository.setOwnerAdminRole(isAdmin)
        _statusMessage.value = if (isAdmin) "Switched role to OWNER_ADMIN" else "Switched role to normal USER"
    }

    fun signOut() {
        repository.clearSession()
        _authStatus.value = AuthStatus.Unauthenticated
        _phoneOtpState.value = PhoneOtpState()
        _statusMessage.value = "Signed out successfully."
    }
}
