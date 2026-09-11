package com.example.unitconverter.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DgPrimaryButton
import com.example.ui.theme.DgBackgroundLight
import com.example.ui.theme.DgBlueLight
import com.example.ui.theme.DgBorderLight
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron
import com.example.unitconverter.engine.UnitConverterEngine
import com.example.unitconverter.model.UnitCategory
import com.example.unitconverter.model.UnitDefinition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    onBack: () -> Unit,
    viewModel: UnitConverterViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DgBackgroundLight)
            .statusBarsPadding()
            .testTag("screen_unit_converter")
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
                    modifier = Modifier.testTag("unit_converter_back_button")
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
                        text = "Unit Converter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "DG with Anup • 10 Measurement Categories",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 10 Categories Chip Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UnitCategory.values().forEach { cat ->
                val isSelected = state.selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectCategory(cat) },
                    label = { Text(cat.title, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DgNavyPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF1F5F9),
                        labelColor = Color(0xFF334155)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = null
                )
            }
        }

        // Notification Banner (Copied)
        AnimatedVisibility(visible = state.copiedNotice != null) {
            state.copiedNotice?.let { notice ->
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
                        Text(text = notice, fontSize = 12.sp, color = Color(0xFF166534), modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Conversion Inputs Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "${state.selectedCategory.title} Conversion",
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark,
                            fontSize = 15.sp
                        )

                        // Numerical Input Field
                        OutlinedTextField(
                            value = state.inputValueString,
                            onValueChange = { viewModel.updateInputString(it) },
                            label = { Text("Enter Value") },
                            placeholder = { Text("1.0") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_unit_value")
                        )

                        // From and To Unit Selectors with Swap Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // From Unit Picker
                            UnitDropdownPicker(
                                label = "From Unit",
                                selectedUnit = state.fromUnit,
                                availableUnits = state.availableUnits,
                                onSelect = { viewModel.setFromUnit(it) },
                                modifier = Modifier.weight(1f)
                            )

                            // Swap Button
                            IconButton(
                                onClick = { viewModel.swapUnits() },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(DgBlueLight)
                                    .testTag("btn_swap_units")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Swap Units",
                                    tint = DgNavyPrimary
                                )
                            }

                            // To Unit Picker
                            UnitDropdownPicker(
                                label = "To Unit",
                                selectedUnit = state.toUnit,
                                availableUnits = state.availableUnits,
                                onSelect = { viewModel.setToUnit(it) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Formatted Result Card
            if (state.conversionResult != null) {
                val res = state.conversionResult!!
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF86EFAC)))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Converted Result",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF166534)
                                )

                                IconButton(
                                    onClick = { viewModel.copyResultToClipboard() },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .testTag("btn_copy_conversion_result")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Result",
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(
                                text = "${UnitConverterEngine.formatNumber(res.convertedValue)} ${res.toUnit.symbol}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D),
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.testTag("text_converted_value")
                            )

                            Text(
                                text = "${UnitConverterEngine.formatNumber(res.inputValue)} ${res.fromUnit.name} = ${UnitConverterEngine.formatNumber(res.convertedValue)} ${res.toUnit.name}",
                                fontSize = 13.sp,
                                color = Color(0xFF166534),
                                fontWeight = FontWeight.Medium
                            )

                            // Formula explainer
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = 0.8f))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Formula: ${res.formulaText}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                // All Units in Category Breakdown Table
                item {
                    Text(
                        text = "Instant Multi-Unit Reference for ${UnitConverterEngine.formatNumber(res.inputValue)} ${res.fromUnit.symbol}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DgNavyDark
                    )
                }

                items(res.allCategoryConversions) { (unit, value) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (unit.id == res.toUnit.id) DgBlueLight else Color.White
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (unit.id == res.toUnit.id) DgNavyPrimary else DgBorderLight
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = unit.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (unit.id == res.toUnit.id) FontWeight.Bold else FontWeight.SemiBold,
                                    color = DgNavyDark
                                )
                                Text(
                                    text = unit.symbol,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Text(
                                text = "${UnitConverterEngine.formatNumber(value)} ${unit.symbol}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (unit.id == res.toUnit.id) DgNavyPrimary else DgNavyDark,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UnitDropdownPicker(
    label: String,
    selectedUnit: UnitDefinition?,
    availableUnits: List<UnitDefinition>,
    onSelect: (UnitDefinition) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(text = label, fontSize = 10.sp, color = Color(0xFF64748B))
                Text(
                    text = selectedUnit?.let { "${it.symbol} (${it.name})" } ?: "Select Unit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = DgNavyDark,
                    maxLines = 1
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableUnits.forEach { unit ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${unit.name} (${unit.symbol})",
                            fontSize = 13.sp,
                            fontWeight = if (unit.id == selectedUnit?.id) FontWeight.Bold else FontWeight.Normal,
                            color = if (unit.id == selectedUnit?.id) DgNavyPrimary else DgNavyDark
                        )
                    },
                    onClick = {
                        onSelect(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}
