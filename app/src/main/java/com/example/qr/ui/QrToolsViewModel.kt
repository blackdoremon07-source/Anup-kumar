package com.example.qr.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr.engine.QrEngine
import com.example.qr.model.QrContactData
import com.example.qr.model.QrEmailData
import com.example.qr.model.QrErrorCorrection
import com.example.qr.model.QrInputType
import com.example.qr.model.QrMargin
import com.example.qr.model.QrResult
import com.example.qr.model.QrSize
import com.example.qr.model.QrSmsData
import com.example.qr.model.QrWifiData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QrUiState(
    val selectedType: QrInputType = QrInputType.URL,

    // Inputs
    val textInput: String = "",
    val urlInput: String = "https://",
    val phoneInput: String = "",
    val emailData: QrEmailData = QrEmailData(),
    val wifiData: QrWifiData = QrWifiData(),
    val smsData: QrSmsData = QrSmsData(),
    val contactData: QrContactData = QrContactData(),

    // Configuration
    val size: QrSize = QrSize.MEDIUM,
    val errorCorrection: QrErrorCorrection = QrErrorCorrection.MEDIUM,
    val margin: QrMargin = QrMargin.STANDARD,

    // Status & Result
    val qrResult: QrResult? = null,
    val isGenerating: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class QrToolsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(QrUiState())
    val uiState: StateFlow<QrUiState> = _uiState.asStateFlow()

    init {
        // Generate initial default QR
        generateQr()
    }

    fun setInputType(type: QrInputType) {
        _uiState.update { it.copy(selectedType = type, errorMessage = null, successMessage = null) }
    }

    fun updateTextInput(text: String) {
        _uiState.update { it.copy(textInput = text, errorMessage = null) }
    }

    fun updateUrlInput(url: String) {
        _uiState.update { it.copy(urlInput = url, errorMessage = null) }
    }

    fun updatePhoneInput(phone: String) {
        _uiState.update { it.copy(phoneInput = phone, errorMessage = null) }
    }

    fun updateEmailData(email: String? = null, subject: String? = null, body: String? = null) {
        _uiState.update { state ->
            state.copy(
                emailData = state.emailData.copy(
                    email = email ?: state.emailData.email,
                    subject = subject ?: state.emailData.subject,
                    body = body ?: state.emailData.body
                ),
                errorMessage = null
            )
        }
    }

    fun updateWifiData(ssid: String? = null, password: String? = null, securityType: String? = null) {
        _uiState.update { state ->
            state.copy(
                wifiData = state.wifiData.copy(
                    ssid = ssid ?: state.wifiData.ssid,
                    password = password ?: state.wifiData.password,
                    securityType = securityType ?: state.wifiData.securityType
                ),
                errorMessage = null
            )
        }
    }

    fun updateSmsData(phone: String? = null, message: String? = null) {
        _uiState.update { state ->
            state.copy(
                smsData = state.smsData.copy(
                    phone = phone ?: state.smsData.phone,
                    message = message ?: state.smsData.message
                ),
                errorMessage = null
            )
        }
    }

    fun updateContactData(
        first: String? = null,
        last: String? = null,
        phone: String? = null,
        email: String? = null,
        org: String? = null,
        title: String? = null
    ) {
        _uiState.update { state ->
            state.copy(
                contactData = state.contactData.copy(
                    firstName = first ?: state.contactData.firstName,
                    lastName = last ?: state.contactData.lastName,
                    phone = phone ?: state.contactData.phone,
                    email = email ?: state.contactData.email,
                    organization = org ?: state.contactData.organization,
                    jobTitle = title ?: state.contactData.jobTitle
                ),
                errorMessage = null
            )
        }
    }

    fun setSize(size: QrSize) {
        _uiState.update { it.copy(size = size) }
        generateQr()
    }

    fun setErrorCorrection(ec: QrErrorCorrection) {
        _uiState.update { it.copy(errorCorrection = ec) }
        generateQr()
    }

    fun setMargin(margin: QrMargin) {
        _uiState.update { it.copy(margin = margin) }
        generateQr()
    }

    fun clearInputs() {
        _uiState.update {
            it.copy(
                textInput = "",
                urlInput = "https://",
                phoneInput = "",
                emailData = QrEmailData(),
                wifiData = QrWifiData(),
                smsData = QrSmsData(),
                contactData = QrContactData(),
                errorMessage = null,
                successMessage = null,
                qrResult = null
            )
        }
    }

    fun generateQr() {
        val state = _uiState.value
        val payloadRes = QrEngine.buildPayload(
            type = state.selectedType,
            textInput = state.textInput,
            urlInput = state.urlInput,
            phoneInput = state.phoneInput,
            emailData = state.emailData,
            wifiData = state.wifiData,
            smsData = state.smsData,
            contactData = state.contactData
        )

        val payload = payloadRes.getOrElse { e ->
            _uiState.update { it.copy(errorMessage = e.message) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, errorMessage = null, successMessage = null) }
            try {
                val result = QrEngine.generateQr(
                    content = payload,
                    size = state.size,
                    errorCorrection = state.errorCorrection,
                    margin = state.margin
                )
                _uiState.update {
                    it.copy(
                        qrResult = result,
                        isGenerating = false,
                        successMessage = "QR Code generated successfully!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        errorMessage = "Failed to generate QR: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun downloadPng() {
        val result = _uiState.value.qrResult ?: return
        viewModelScope.launch {
            val saveResult = QrEngine.saveQrPng(getApplication(), result.bitmap)
            _uiState.update {
                it.copy(
                    successMessage = saveResult.getOrElse { e -> "PNG Download failed: ${e.localizedMessage}" }
                )
            }
        }
    }

    fun downloadSvg() {
        val result = _uiState.value.qrResult ?: return
        viewModelScope.launch {
            val saveResult = QrEngine.saveQrSvg(getApplication(), result.svgString)
            _uiState.update {
                it.copy(
                    successMessage = saveResult.getOrElse { e -> "SVG Download failed: ${e.localizedMessage}" }
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
