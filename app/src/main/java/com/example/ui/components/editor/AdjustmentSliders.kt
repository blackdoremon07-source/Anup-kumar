package com.example.ui.components.editor

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.model.ImageAdjustments
import com.example.ui.theme.DgBorderLight
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron
import kotlin.math.roundToInt

@Composable
fun AdjustmentSliders(
    adjustments: ImageAdjustments,
    onAdjustmentsChanged: (ImageAdjustments) -> Unit,
    onResetAdjustments: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Image Adjustments / इमेज एडजस्टमेंट",
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Fine-tune light, contrast, sharpness and tone",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }

                OutlinedButton(
                    onClick = onResetAdjustments,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("reset_adjustments_button")
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(14.dp), tint = DgNavyPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", fontSize = 11.sp, color = DgNavyPrimary, fontWeight = FontWeight.SemiBold)
                }
            }

            // 1. Brightness: -100 to +100
            AdjustmentSliderRow(
                label = "Brightness (चमक)",
                value = adjustments.brightness,
                min = -100f,
                max = 100f,
                unit = "",
                onValueChange = { onAdjustmentsChanged(adjustments.copy(brightness = it.roundToInt())) }
            )

            // 2. Contrast: -100 to +100
            AdjustmentSliderRow(
                label = "Contrast (कंट्रास्ट)",
                value = adjustments.contrast,
                min = -100f,
                max = 100f,
                unit = "",
                onValueChange = { onAdjustmentsChanged(adjustments.copy(contrast = it.roundToInt())) }
            )

            // 3. Saturation: -100 to +100
            AdjustmentSliderRow(
                label = "Saturation (रंग संतृप्ति)",
                value = adjustments.saturation,
                min = -100f,
                max = 100f,
                unit = "",
                onValueChange = { onAdjustmentsChanged(adjustments.copy(saturation = it.roundToInt())) }
            )

            // 4. Blur: 0 to 20
            AdjustmentSliderRow(
                label = "Blur (धुंधलापन)",
                value = adjustments.blur,
                min = 0f,
                max = 20f,
                unit = "px",
                onValueChange = { onAdjustmentsChanged(adjustments.copy(blur = it.roundToInt())) }
            )

            // 5. Sharpen: 0 to 100
            AdjustmentSliderRow(
                label = "Sharpen (तीक्ष्णता)",
                value = adjustments.sharpen,
                min = 0f,
                max = 100f,
                unit = "%",
                onValueChange = { onAdjustmentsChanged(adjustments.copy(sharpen = it.roundToInt())) }
            )

            // 6. Grayscale: 0 to 100
            AdjustmentSliderRow(
                label = "Grayscale (श्वेत-श्याम)",
                value = adjustments.grayscale,
                min = 0f,
                max = 100f,
                unit = "%",
                onValueChange = { onAdjustmentsChanged(adjustments.copy(grayscale = it.roundToInt())) }
            )

            // 7. Opacity: 0 to 100
            AdjustmentSliderRow(
                label = "Opacity (पारदर्शिता)",
                value = adjustments.opacity,
                min = 0f,
                max = 100f,
                unit = "%",
                onValueChange = { onAdjustmentsChanged(adjustments.copy(opacity = it.roundToInt())) }
            )
        }
    }
}

@Composable
fun AdjustmentSliderRow(
    label: String,
    value: Int,
    min: Float,
    max: Float,
    unit: String,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155)
            )

            Box(
                modifier = Modifier
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${if (value > 0 && min < 0) "+" else ""}$value$unit",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (value != 0 && (min == 0f && value != 100 && label.startsWith("Opacity") || value != 0 && !label.startsWith("Opacity"))) DgSaffron else DgNavyDark
                )
            }
        }

        Slider(
            value = value.toFloat(),
            onValueChange = onValueChange,
            valueRange = min..max,
            colors = SliderDefaults.colors(
                thumbColor = DgNavyPrimary,
                activeTrackColor = DgNavyPrimary,
                inactiveTrackColor = Color(0xFFE2E8F0)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
        )
    }
}
