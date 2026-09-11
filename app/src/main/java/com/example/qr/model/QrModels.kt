package com.example.qr.model

import android.graphics.Bitmap
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

enum class QrInputType(val label: String, val iconDesc: String) {
    TEXT("Text", "Plain text, notes, roll numbers"),
    URL("URL / Web", "Website links, portal links"),
    PHONE("Phone", "Direct dial phone number"),
    EMAIL("Email", "Pre-composed email address"),
    WIFI("Wi-Fi", "Wi-Fi network connection details"),
    SMS("SMS", "Direct SMS phone and message"),
    CONTACT("Contact (vCard)", "Digital business card")
}

enum class QrSize(val label: String, val dimensionPx: Int) {
    SMALL("Small (256 × 256)", 256),
    MEDIUM("Medium (512 × 512)", 512),
    LARGE("Large (1024 × 1024)", 1024)
}

enum class QrErrorCorrection(val label: String, val level: ErrorCorrectionLevel, val description: String) {
    LOW("L (7% recovery)", ErrorCorrectionLevel.L, "Best for clean digital screens"),
    MEDIUM("M (15% recovery)", ErrorCorrectionLevel.M, "Standard balanced recovery"),
    QUARTILE("Q (25% recovery)", ErrorCorrectionLevel.Q, "High redundancy for printed cards"),
    HIGH("H (30% recovery)", ErrorCorrectionLevel.H, "Maximum tolerance for harsh environments")
}

enum class QrMargin(val label: String, val value: Int) {
    MINIMAL("1 Module", 1),
    STANDARD("2 Modules", 2),
    GENEROUS("4 Modules (Official)", 4)
}

data class QrWifiData(
    val ssid: String = "",
    val password: String = "",
    val securityType: String = "WPA" // WPA, WEP, nopass
)

data class QrContactData(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val organization: String = "",
    val jobTitle: String = ""
)

data class QrSmsData(
    val phone: String = "",
    val message: String = ""
)

data class QrEmailData(
    val email: String = "",
    val subject: String = "",
    val body: String = ""
)

data class QrResult(
    val contentString: String,
    val bitmap: Bitmap,
    val svgString: String,
    val sizePx: Int,
    val errorCorrection: QrErrorCorrection,
    val margin: QrMargin
)
