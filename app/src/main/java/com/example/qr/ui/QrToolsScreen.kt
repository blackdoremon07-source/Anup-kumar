package com.example.qr.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qr.model.QrErrorCorrection
import com.example.qr.model.QrInputType
import com.example.qr.model.QrMargin
import com.example.qr.model.QrSize
import com.example.ui.components.DgOutlinedButton
import com.example.ui.components.DgPrimaryButton
import com.example.ui.theme.DgBackgroundLight
import com.example.ui.theme.DgBlueLight
import com.example.ui.theme.DgBorderLight
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrToolsScreen(
    onBack: () -> Unit,
    viewModel: QrToolsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DgBackgroundLight)
            .statusBarsPadding()
            .testTag("screen_qr_tools")
    ) {
        // App Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("qr_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DgNavyPrimary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "QR Code Generator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "DG with Anup • Fast Offline Generator",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Safe",
                    tint = DgEmerald,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Messages
        AnimatedVisibility(visible = state.errorMessage != null) {
            state.errorMessage?.let { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFCA5A5)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = msg, fontSize = 12.sp, color = Color(0xFF991B1B), modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        AnimatedVisibility(visible = state.successMessage != null) {
            state.successMessage?.let { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF86EFAC)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = msg, fontSize = 12.sp, color = Color(0xFF166534), modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Type Selector Chips
            item {
                Text(
                    text = "Select Data Type",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DgNavyDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QrInputType.values().forEach { type ->
                        val isSelected = state.selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setInputType(type) },
                            label = { Text(type.label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DgNavyPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = Color(0xFF334155)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) DgNavyPrimary else DgBorderLight
                            )
                        )
                    }
                }
            }

            // Input Form Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        when (state.selectedType) {
                            QrInputType.TEXT -> {
                                Text("Enter Plain Text or Roll Number", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DgNavyDark)
                                OutlinedTextField(
                                    value = state.textInput,
                                    onValueChange = { viewModel.updateTextInput(it) },
                                    placeholder = { Text("e.g., SSC CGL Registration # 202610489") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .testTag("input_qr_text")
                                )
                            }

                            QrInputType.URL -> {
                                Text("Enter Website URL", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DgNavyDark)
                                OutlinedTextField(
                                    value = state.urlInput,
                                    onValueChange = { viewModel.updateUrlInput(it) },
                                    placeholder = { Text("https://upsc.gov.in") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_qr_url")
                                )
                                Text(
                                    text = "Must be a valid web link (e.g., https://example.com)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            QrInputType.PHONE -> {
                                Text("Enter Phone Number", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DgNavyDark)
                                OutlinedTextField(
                                    value = state.phoneInput,
                                    onValueChange = { viewModel.updatePhoneInput(it) },
                                    placeholder = { Text("+91 9876543210") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_qr_phone")
                                )
                            }

                            QrInputType.EMAIL -> {
                                Text("Compose Email Data", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DgNavyDark)
                                OutlinedTextField(
                                    value = state.emailData.email,
                                    onValueChange = { viewModel.updateEmailData(email = it) },
                                    placeholder = { Text("Recipient email: helpdesk@portal.gov.in") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = state.emailData.subject,
                                    onValueChange = { viewModel.updateEmailData(subject = it) },
                                    placeholder = { Text("Subject (optional)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = state.emailData.body,
                                    onValueChange = { viewModel.updateEmailData(body = it) },
                                    placeholder = { Text("Email Message Body (optional)") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                )
                            }

                            QrInputType.WIFI -> {
                                Text("Enter Wi-Fi Network Credentials", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DgNavyDark)
                                OutlinedTextField(
                                    value = state.wifiData.ssid,
                                    onValueChange = { viewModel.updateWifiData(ssid = it) },
                                    placeholder = { Text("Wi-Fi Network Name (SSID)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = state.wifiData.password,
                                    onValueChange = { viewModel.updateWifiData(password = it) },
                                    placeholder = { Text("Wi-Fi Password") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("WPA", "WEP", "nopass").forEach { sec ->
                                        FilterChip(
                                            selected = state.wifiData.securityType.equals(sec, ignoreCase = true),
                                            onClick = { viewModel.updateWifiData(securityType = sec) },
                                            label = { Text(sec.uppercase(), fontSize = 11.sp) }
                                        )
                                    }
                                }
                            }

                            QrInputType.SMS -> {
                                Text("Enter SMS Target & Message", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DgNavyDark)
                                OutlinedTextField(
                                    value = state.smsData.phone,
                                    onValueChange = { viewModel.updateSmsData(phone = it) },
                                    placeholder = { Text("Recipient Phone Number") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = state.smsData.message,
                                    onValueChange = { viewModel.updateSmsData(message = it) },
                                    placeholder = { Text("SMS Message Text") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                )
                            }

                            QrInputType.CONTACT -> {
                                Text("Contact (vCard) Information", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DgNavyDark)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = state.contactData.firstName,
                                        onValueChange = { viewModel.updateContactData(first = it) },
                                        placeholder = { Text("First Name") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = state.contactData.lastName,
                                        onValueChange = { viewModel.updateContactData(last = it) },
                                        placeholder = { Text("Last Name") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }
                                OutlinedTextField(
                                    value = state.contactData.phone,
                                    onValueChange = { viewModel.updateContactData(phone = it) },
                                    placeholder = { Text("Phone Number") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = state.contactData.email,
                                    onValueChange = { viewModel.updateContactData(email = it) },
                                    placeholder = { Text("Email Address") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = state.contactData.organization,
                                    onValueChange = { viewModel.updateContactData(org = it) },
                                    placeholder = { Text("Organization / Department") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            // QR Customization Controls (Size, Error Correction, Margin)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("QR Parameters", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DgNavyDark)

                        // Size
                        Column {
                            Text("Resolution / Size", fontSize = 11.sp, color = Color(0xFF64748B))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                QrSize.values().forEach { size ->
                                    FilterChip(
                                        selected = state.size == size,
                                        onClick = { viewModel.setSize(size) },
                                        label = { Text(size.label, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }

                        // Error Correction
                        Column {
                            Text("Error Correction Redundancy", fontSize = 11.sp, color = Color(0xFF64748B))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                QrErrorCorrection.values().forEach { ec ->
                                    FilterChip(
                                        selected = state.errorCorrection == ec,
                                        onClick = { viewModel.setErrorCorrection(ec) },
                                        label = { Text(ec.label, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }

                        // Margin
                        Column {
                            Text("Quiet Zone / Margin", fontSize = 11.sp, color = Color(0xFF64748B))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                QrMargin.values().forEach { margin ->
                                    FilterChip(
                                        selected = state.margin == margin,
                                        onClick = { viewModel.setMargin(margin) },
                                        label = { Text(margin.label, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Generate & Clear Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DgOutlinedButton(
                        text = "Clear",
                        onClick = { viewModel.clearInputs() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_clear_qr"),
                        leadingIcon = Icons.Default.Clear
                    )

                    DgPrimaryButton(
                        text = "Generate QR",
                        onClick = { viewModel.generateQr() },
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("btn_generate_qr"),
                        leadingIcon = Icons.Default.QrCode
                    )
                }
            }

            // QR Preview & Download Section
            if (state.qrResult != null) {
                val res = state.qrResult!!
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Generated QR Code",
                                fontWeight = FontWeight.Bold,
                                color = DgNavyDark,
                                fontSize = 15.sp
                            )

                            // High-contrast QR matrix container
                            Box(
                                modifier = Modifier
                                    .size(240.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = res.bitmap.asImageBitmap(),
                                    contentDescription = "QR Code Matrix",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            // Payload preview
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = res.contentString,
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // Specifications tag
                            Text(
                                text = "${res.sizePx} × ${res.sizePx} px • Error Correction: ${res.errorCorrection.name} • Margin: ${res.margin.value}",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )

                            // Downloads
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DgPrimaryButton(
                                    text = "Download PNG",
                                    onClick = { viewModel.downloadPng() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_download_qr_png"),
                                    leadingIcon = Icons.Default.Download
                                )

                                DgOutlinedButton(
                                    text = "Download SVG",
                                    onClick = { viewModel.downloadSvg() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_download_qr_svg"),
                                    leadingIcon = Icons.Default.Download
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
