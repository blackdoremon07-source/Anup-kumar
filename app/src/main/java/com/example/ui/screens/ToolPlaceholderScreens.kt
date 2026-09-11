package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DgOutlinedButton
import com.example.ui.components.DgPrimaryButton
import com.example.ui.theme.DgAmber
import com.example.ui.theme.DgAmberLight
import com.example.ui.theme.DgBackgroundLight
import com.example.ui.theme.DgBlueLight
import com.example.ui.theme.DgBorderLight
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron

@Composable
fun ToolTemplateScaffold(
    title: String,
    category: String,
    icon: ImageVector,
    partNotice: String,
    description: String,
    features: List<String>,
    onBack: () -> Unit,
    testTag: String,
    previewContent: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DgBackgroundLight)
            .statusBarsPadding()
            .testTag(testTag)
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
                    modifier = Modifier.testTag("tool_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DgNavyPrimary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 17.sp
                    )
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Scrollable Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Part Roadmap Notice Badge
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DgAmberLight),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFDE68A)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFB45309),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "$partNotice • Foundational UI & routes ready in Part 1.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF92400E)
                    )
                }
            }

            // Preview Canvas Placeholder Area
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    previewContent()
                }
            }

            // Description & Upcoming Features
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Tool Overview",
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Key Capabilities",
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    features.forEach { feature ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = DgEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = feature,
                                fontSize = 12.sp,
                                color = Color(0xFF334155)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Part 2: PhotoEditorScreen has been fully implemented in com.example.ui.screens.photoeditor.PhotoEditorScreen

// 2. Government Job Photo & Signature Maker Placeholder (Part 3)
@Composable
fun JobPhotoSignatureScreen(onBack: () -> Unit) {
    ToolTemplateScaffold(
        title = "Govt Job Photo & Signature Maker",
        category = "Govt Spec Standards",
        icon = Icons.Default.Star,
        partNotice = "Engine arriving in Part 3",
        description = "Strict dimensional and byte-size conformance engine for SSC, UPSC, IBPS, and Railway portals. Generates date-stamped passport photos and crisp signature scans.",
        features = listOf(
            "Preset profiles for SSC CGL/CHSL, UPSC OTR, IBPS PO, RRB",
            "Automatic Date of Photo (DOP) & Candidate Name imprint",
            "Signature background cleanup to pure white",
            "Guaranteed under 20KB - 50KB size optimization"
        ),
        onBack = onBack,
        testTag = "screen_job_photo_signature"
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFEF3C7))
                .border(1.5.dp, Color(0xFFFCD34D), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFB45309),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Govt Job Photo & Signature Presets",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E),
                    fontSize = 14.sp
                )
                Text(
                    text = "Engine activating in Part 3",
                    fontSize = 11.sp,
                    color = Color(0xFF78350F)
                )
            }
        }
    }
}

// 3. PDF Tools Placeholder (Part 4)
@Composable
fun PdfToolsScreen(onBack: () -> Unit) {
    ToolTemplateScaffold(
        title = "PDF Tools & Compressor",
        category = "Document Management",
        icon = Icons.Default.PictureAsPdf,
        partNotice = "Engine arriving in Part 4",
        description = "Unified PDF toolkit to merge multi-page marksheets, compress certificates under 200KB for upload, and convert mobile photos directly to clean PDF documents.",
        features = listOf(
            "Merge educational certificates into a single upload",
            "Smart lossy compression to under 200KB / 300KB",
            "Multi-page Image to PDF conversion",
            "PDF preview and page rotation"
        ),
        onBack = onBack,
        testTag = "screen_pdf_tools"
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFEE2E2))
                .border(1.5.dp, Color(0xFFFCA5A5), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Document PDF Engine Preview",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF991B1B),
                    fontSize = 14.sp
                )
                Text(
                    text = "Engine activating in Part 4",
                    fontSize = 11.sp,
                    color = Color(0xFF7F1D1D)
                )
            }
        }
    }
}

// 4. QR Code Generator Placeholder (Part 4)
@Composable
fun QrToolsScreen(onBack: () -> Unit) {
    ToolTemplateScaffold(
        title = "QR Code Generator",
        category = "Utility",
        icon = Icons.Default.QrCode,
        partNotice = "Engine arriving in Part 4",
        description = "Create instant, shareable QR codes for application tracking links, roll number cards, payment reference numbers, and candidate contact cards.",
        features = listOf(
            "Generate QR codes for text, URLs, and phone numbers",
            "High resolution PNG export with DG with Anup badge",
            "Offline generation with zero server lag"
        ),
        onBack = onBack,
        testTag = "screen_qr_tools"
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF1F5F9))
                .border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = DgNavyDark,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "QR Matrix Generator Preview",
                    fontWeight = FontWeight.Bold,
                    color = DgNavyDark,
                    fontSize = 14.sp
                )
                Text(
                    text = "Engine activating in Part 4",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

// 5. Calculator Placeholder (Part 4)
@Composable
fun CalculatorScreen(onBack: () -> Unit) {
    ToolTemplateScaffold(
        title = "Age & Eligibility Calculator",
        category = "Calculators & Math",
        icon = Icons.Default.Calculate,
        partNotice = "Engine arriving in Part 4",
        description = "Calculate exact age in years, months, and days as of any official notification cutoff date (e.g., 01 August 2026), and calculate marks percentage.",
        features = listOf(
            "Exact Age Calculator as on notification cutoff date",
            "CGPA to Percentage official formulas (CBSE / State Boards)",
            "Category age relaxation threshold validator"
        ),
        onBack = onBack,
        testTag = "screen_calculator"
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F3FF))
                .border(1.5.dp, Color(0xFFDDD6FE), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = Color(0xFF7C3AED),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cutoff & Age Calculator Preview",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5B21B6),
                    fontSize = 14.sp
                )
                Text(
                    text = "Engine activating in Part 4",
                    fontSize = 11.sp,
                    color = Color(0xFF4C1D95)
                )
            }
        }
    }
}

// 6. Unit Converter Placeholder (Part 4)
@Composable
fun UnitConverterScreen(onBack: () -> Unit) {
    ToolTemplateScaffold(
        title = "Unit & Resolution Converter",
        category = "Conversion Utility",
        icon = Icons.Default.SwapHoriz,
        partNotice = "Engine arriving in Part 4",
        description = "Seamlessly convert dimensions between pixels, centimeters, and inches at 200 DPI, 300 DPI, or 600 DPI, and calculate file storage units.",
        features = listOf(
            "Pixels to Centimeters at 200/300 DPI",
            "File size conversion (KB, MB, Bytes)",
            "Resolution presets matching portal requirements"
        ),
        onBack = onBack,
        testTag = "screen_unit_converter"
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFECFDF5))
                .border(1.5.dp, Color(0xFFA7F3D0), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = DgEmerald,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "DPI & Unit Converter Preview",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF065F46),
                    fontSize = 14.sp
                )
                Text(
                    text = "Engine activating in Part 4",
                    fontSize = 11.sp,
                    color = Color(0xFF047857)
                )
            }
        }
    }
}
