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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.model.BackgroundColorOption
import com.example.editor.model.FilterType
import com.example.editor.model.OutputFormat
import com.example.ui.theme.DgBorderLight
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron

@Composable
fun FilterPresetSelector(
    selectedFilter: FilterType,
    onFilterSelected: (FilterType) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Preset Filters / प्रीसेट फिल्टर्स",
                fontWeight = FontWeight.Bold,
                color = DgNavyDark,
                fontSize = 15.sp
            )
            Text(
                text = "One-tap studio enhancement styles",
                color = Color(0xFF64748B),
                fontSize = 11.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterType.values().forEach { filter ->
                    val isSelected = filter == selectedFilter
                    FilterChipCard(
                        filter = filter,
                        isSelected = isSelected,
                        onClick = { onFilterSelected(filter) }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChipCard(
    filter: FilterType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val gradient = when (filter) {
        FilterType.ORIGINAL -> Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7)))
        FilterType.GRAYSCALE -> Brush.linearGradient(listOf(Color(0xFF94A3B8), Color(0xFF475569)))
        FilterType.VINTAGE -> Brush.linearGradient(listOf(Color(0xFFD97706), Color(0xFF78350F)))
        FilterType.WARM -> Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFDC2626)))
        FilterType.COOL -> Brush.linearGradient(listOf(Color(0xFF06B6D4), Color(0xFF3B82F6)))
        FilterType.BRIGHT -> Brush.linearGradient(listOf(Color(0xFFFDE047), Color(0xFFCA8A04)))
        FilterType.HIGH_CONTRAST -> Brush.linearGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
        FilterType.SOFT -> Brush.linearGradient(listOf(Color(0xFFF472B6), Color(0xFFFB7185)))
    }

    Column(
        modifier = Modifier
            .width(76.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) DgNavyPrimary else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp)
            .testTag("filter_${filter.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = filter.displayName,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) DgNavyPrimary else Color(0xFF475569),
            maxLines = 1
        )
    }
}

@Composable
fun BackgroundSelector(
    selectedBackground: BackgroundColorOption,
    outputFormat: OutputFormat,
    onBackgroundSelected: (BackgroundColorOption) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Background / पृष्ठभूमि",
                fontWeight = FontWeight.Bold,
                color = DgNavyDark,
                fontSize = 15.sp
            )
            Text(
                text = "Choose backdrop fill (JPG automatically fills transparency with white)",
                color = Color(0xFF64748B),
                fontSize = 11.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BackgroundColorOption.values().forEach { option ->
                    val isSelected = option == selectedBackground
                    val isDisabled = option == BackgroundColorOption.TRANSPARENT && !outputFormat.supportsTransparency

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) DgNavyPrimary else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable(enabled = true) { onBackgroundSelected(option) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (option) {
                                            BackgroundColorOption.WHITE -> Color.White
                                            BackgroundColorOption.BLACK -> Color.Black
                                            BackgroundColorOption.TRANSPARENT -> Color(0xFFCBD5E1)
                                            BackgroundColorOption.ORIGINAL -> DgSaffron
                                        }
                                    )
                                    .border(1.dp, Color(0xFF94A3B8), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (option == BackgroundColorOption.WHITE) Color.Black else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = option.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isDisabled) Color(0xFF94A3B8) else DgNavyDark
                            )
                        }
                    }
                }
            }

            if (selectedBackground == BackgroundColorOption.TRANSPARENT && !outputFormat.supportsTransparency) {
                Text(
                    text = "Notice: Current output format (${outputFormat.name}) does not support alpha transparency. Transparent pixels will be exported with solid white background.",
                    fontSize = 11.sp,
                    color = DgSaffron,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
