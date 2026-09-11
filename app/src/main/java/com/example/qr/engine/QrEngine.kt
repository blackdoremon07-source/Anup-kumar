package com.example.qr.engine

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.qr.model.QrContactData
import com.example.qr.model.QrEmailData
import com.example.qr.model.QrErrorCorrection
import com.example.qr.model.QrInputType
import com.example.qr.model.QrMargin
import com.example.qr.model.QrResult
import com.example.qr.model.QrSize
import com.example.qr.model.QrSmsData
import com.example.qr.model.QrWifiData
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Pattern

object QrEngine {

    private val URL_PATTERN = Pattern.compile(
        "^(https?://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:\\d+)?(/.*)?$",
        Pattern.CASE_INSENSITIVE
    )

    fun validateUrl(input: String): Pair<Boolean, String> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return Pair(false, "URL cannot be empty")
        val matcher = URL_PATTERN.matcher(trimmed)
        return if (matcher.matches()) {
            val normalized = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                "https://$trimmed"
            } else trimmed
            Pair(true, normalized)
        } else {
            Pair(false, "Please enter a valid website URL (e.g., https://example.gov.in)")
        }
    }

    fun buildPayload(
        type: QrInputType,
        textInput: String,
        urlInput: String,
        phoneInput: String,
        emailData: QrEmailData,
        wifiData: QrWifiData,
        smsData: QrSmsData,
        contactData: QrContactData
    ): Result<String> {
        return try {
            val payload = when (type) {
                QrInputType.TEXT -> {
                    if (textInput.isBlank()) return Result.failure(Exception("Text cannot be blank"))
                    textInput.trim()
                }
                QrInputType.URL -> {
                    val (valid, normalized) = validateUrl(urlInput)
                    if (!valid) return Result.failure(Exception(normalized))
                    normalized
                }
                QrInputType.PHONE -> {
                    val cleanPhone = phoneInput.trim()
                    if (cleanPhone.isBlank()) return Result.failure(Exception("Phone number cannot be blank"))
                    "tel:$cleanPhone"
                }
                QrInputType.EMAIL -> {
                    if (emailData.email.isBlank()) return Result.failure(Exception("Email cannot be blank"))
                    val params = mutableListOf<String>()
                    if (emailData.subject.isNotBlank()) params.add("subject=" + Uri.encode(emailData.subject))
                    if (emailData.body.isNotBlank()) params.add("body=" + Uri.encode(emailData.body))
                    val query = if (params.isNotEmpty()) "?" + params.joinToString("&") else ""
                    "mailto:${emailData.email.trim()}$query"
                }
                QrInputType.WIFI -> {
                    if (wifiData.ssid.isBlank()) return Result.failure(Exception("Wi-Fi SSID is required"))
                    val sec = when (wifiData.securityType.uppercase()) {
                        "NOPASS", "OPEN" -> "nopass"
                        "WEP" -> "WEP"
                        else -> "WPA"
                    }
                    val passPart = if (sec != "nopass" && wifiData.password.isNotBlank()) "P:${wifiData.password};" else ""
                    "WIFI:T:$sec;S:${wifiData.ssid};$passPart;"
                }
                QrInputType.SMS -> {
                    if (smsData.phone.isBlank()) return Result.failure(Exception("Recipient phone is required"))
                    "smsto:${smsData.phone.trim()}:${smsData.message}"
                }
                QrInputType.CONTACT -> {
                    if (contactData.firstName.isBlank() && contactData.lastName.isBlank() && contactData.phone.isBlank()) {
                        return Result.failure(Exception("Please provide at least a name or phone number"))
                    }
                    val vcard = StringBuilder()
                    vcard.append("BEGIN:VCARD\n")
                    vcard.append("VERSION:3.0\n")
                    vcard.append("N:${contactData.lastName};${contactData.firstName};;;\n")
                    vcard.append("FN:${contactData.firstName} ${contactData.lastName}".trim() + "\n")
                    if (contactData.organization.isNotBlank()) vcard.append("ORG:${contactData.organization}\n")
                    if (contactData.jobTitle.isNotBlank()) vcard.append("TITLE:${contactData.jobTitle}\n")
                    if (contactData.phone.isNotBlank()) vcard.append("TEL:${contactData.phone}\n")
                    if (contactData.email.isNotBlank()) vcard.append("EMAIL:${contactData.email}\n")
                    vcard.append("END:VCARD")
                    vcard.toString()
                }
            }
            Result.success(payload)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateQr(
        content: String,
        size: QrSize,
        errorCorrection: QrErrorCorrection,
        margin: QrMargin
    ): QrResult = withContext(Dispatchers.Default) {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to errorCorrection.level,
            EncodeHintType.MARGIN to margin.value,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )

        val bitMatrix = QRCodeWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            size.dimensionPx,
            size.dimensionPx,
            hints
        )

        val w = bitMatrix.width
        val h = bitMatrix.height
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        val svgPathBuilder = StringBuilder()
        for (x in 0 until w) {
            for (y in 0 until h) {
                if (bitMatrix.get(x, y)) {
                    bitmap.setPixel(x, y, Color.BLACK)
                    svgPathBuilder.append("M$x,${y}h1v1h-1z ")
                } else {
                    bitmap.setPixel(x, y, Color.WHITE)
                }
            }
        }

        val svgString = """
            <?xml version="1.0" encoding="UTF-8"?>
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 $w $h" width="${size.dimensionPx}" height="${size.dimensionPx}">
                <rect width="$w" height="$h" fill="#FFFFFF"/>
                <path fill="#0A192F" d="${svgPathBuilder.toString().trim()}"/>
            </svg>
        """.trimIndent()

        QrResult(
            contentString = content,
            bitmap = bitmap,
            svgString = svgString,
            sizePx = size.dimensionPx,
            errorCorrection = errorCorrection,
            margin = margin
        )
    }

    suspend fun saveQrPng(context: Context, bitmap: Bitmap, fileName: String = "DG_QR_${System.currentTimeMillis()}.png"): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DG_QR")
                }
                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return@withContext Result.failure(Exception("Failed to create image URI"))

                context.contentResolver.openOutputStream(uri)?.use { os ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                }
                Result.success("Saved to Pictures/DG_QR/$fileName")
            } else {
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val targetFile = File(dir, fileName)
                FileOutputStream(targetFile).use { os ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                }
                Result.success("Saved to ${targetFile.absolutePath}")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveQrSvg(context: Context, svgString: String, fileName: String = "DG_QR_${System.currentTimeMillis()}.svg"): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/svg+xml")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return@withContext Result.failure(Exception("Failed to create download URI"))

                context.contentResolver.openOutputStream(uri)?.use { os ->
                    os.write(svgString.toByteArray(Charsets.UTF_8))
                }
                Result.success("Saved to Downloads/$fileName")
            } else {
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val targetFile = File(dir, fileName)
                FileOutputStream(targetFile).use { it.write(svgString.toByteArray(Charsets.UTF_8)) }
                Result.success("Saved to ${targetFile.absolutePath}")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
