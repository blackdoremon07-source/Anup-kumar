package com.example.ui.components.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.model.OutputFormat
import com.example.editor.model.TargetSizeResult
import com.example.editor.model.TargetSizeStatus
import com.example.ui.theme.DgBorderLight
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron

@Composable
fun SizeOptimizerCard(
    outputFormat: OutputFormat,
    onFormatChanged: (OutputFormat) -> Unit,
    quality: Int,
    onQualityChanged: (Int) -> Unit,
    estimatedSizeBytes: Long,
    isTargetSizeEnabled: Boolean,
    onToggleTargetSize: (Boolean) -> Unit,
    targetSizeKb: Int,
    onTargetSizeChanged: (Int) -> Unit,
    targetResult: TargetSizeResult?,
    modifier: Modifier = Modifier
) {
    val targetPresets = listOf(10, 20, 30, 40, 50, 70, 100, 200, 500)
    var customInputText by remember { mutableStateOf("") }
    var isCustomSelected by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Format & Quality / प्रारूप एवं गुणवत्ता",
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Encoding compression and file size controls",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${String.format("%.1f", estimatedSizeBytes / 1024f)} KB",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DgNavyPrimary
                    )
                }
            }

            // 1. Output Format Selector
            Text(
                text = "Export Format:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutputFormat.values().forEach { format ->
                    val isSelected = format == outputFormat
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) DgNavyPrimary else Color(0xFFF8FAFC))
                            .border(1.dp, if (isSelected) DgNavyPrimary else Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                            .clickable { onFormatChanged(format) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = format.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else DgNavyDark
                        )
                    }
                }
            }

            // Quality Slider
            if (outputFormat != OutputFormat.PNG) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Compression Quality: $quality%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = if (quality >= 80) "High Definition" else if (quality >= 50) "Balanced" else "Maximum Compression",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Slider(
                        value = quality.toFloat(),
                        onValueChange = { onQualityChanged(it.toInt()) },
                        valueRange = 1f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = DgNavyPrimary,
                            activeTrackColor = DgNavyPrimary,
                            inactiveTrackColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth().height(24.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "PNG uses lossless DEFLATE compression. Quality slider is bypassed to retain crisp transparency and pixel clarity.",
                        fontSize = 11.sp,
                        color = Color(0xFF475569)
                    )
                }
            }

            // Target File Size Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Compress, contentDescription = null, tint = DgSaffron, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Target File Size Optimizer",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DgNavyDark
                        )
                        Text(
                            text = "Auto-tunes compression to fit portal limits (e.g. 50KB)",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Switch(
                    checked = isTargetSizeEnabled,
                    onCheckedChange = onToggleTargetSize,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = DgEmerald
                    )
                )
            }

            // Target Size Presets & Status
            if (isTargetSizeEnabled) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Choose Target Size (KB):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        targetPresets.forEach { kb ->
                            val isSelected = !isCustomSelected && targetSizeKb == kb
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) DgSaffron else Color(0xFFF1F5F9))
                                    .clickable {
                                        isCustomSelected = false
                                        onTargetSizeChanged(kb)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$kb KB",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else DgNavyDark
                                )
                            }
                        }

                        // Custom option
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isCustomSelected) DgSaffron else Color(0xFFF1F5F9))
                                .clickable { isCustomSelected = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Custom",
                                fontSize = 11.sp,
                                fontWeight = if (isCustomSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCustomSelected) Color.White else DgNavyDark
                            )
                        }
                    }

                    if (isCustomSelected) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customInputText,
                                onValueChange = { input ->
                                    val filtered = input.filter { it.isDigit() }
                                    customInputText = filtered
                                    filtered.toIntOrNull()?.let { kbVal ->
                                        if (kbVal in 5..5000) onTargetSizeChanged(kbVal)
                                    }
                                },
                                label = { Text("Custom KB (e.g. 45)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).height(50.dp),
                                singleLine = true
                            )
                            Text(
                                text = "Valid: 5 - 5000 KB",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    // Status Banner
                    if (targetResult != null) {
                        val statusBg = when (targetResult.status) {
                            TargetSizeStatus.PASS -> Color(0xFFDCFCE7)
                            TargetSizeStatus.CLOSE -> Color(0xFFFEF3C7)
                            TargetSizeStatus.ABOVE_TARGET -> Color(0xFFFFE4E6)
                        }
                        val statusText = when (targetResult.status) {
                            TargetSizeStatus.PASS -> Color(0xFF166534)
                            TargetSizeStatus.CLOSE -> Color(0xFF92400E)
                            TargetSizeStatus.ABOVE_TARGET -> Color(0xFF9F1239)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(statusBg)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Target: ${targetResult.targetKb} KB  |  Actual: ${String.format("%.1f", targetResult.actualKb)} KB",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = statusText
                                    )
                                    Text(
                                        text = "Auto-tuned compression quality to ${targetResult.qualityUsed}%",
                                        fontSize = 10.sp,
                                        color = statusText
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(statusText)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = targetResult.status.name.replace("_", " "),
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
