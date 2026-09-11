package com.example.calculator.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.calculator.model.CalculationHistoryItem
import com.example.calculator.model.CalculatorMode
import com.example.ui.components.DgOutlinedButton
import com.example.ui.theme.DgBackgroundLight
import com.example.ui.theme.DgBorderLight
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    onBack: () -> Unit,
    viewModel: CalculatorViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .testTag("screen_calculator")
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
                    modifier = Modifier.testTag("calculator_back_button")
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
                        text = "Smart Calculator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "DG with Anup • High Precision & History",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleHistorySheet(true) },
                    modifier = Modifier.testTag("btn_calculator_history")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = DgNavyDark
                    )
                }
            }
        }

        // Display Card (Screen)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Expression display
                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = if (state.expression.isEmpty()) "0" else state.expression,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (state.expression.isEmpty()) Color(0xFF94A3B8) else DgNavyDark,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.testTag("calculator_expression_display")
                    )
                }

                // Result Preview or Error
                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage!!,
                        fontSize = 14.sp,
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Medium
                    )
                } else if (state.resultPreview.isNotEmpty() && state.resultPreview != state.expression) {
                    Text(
                        text = "= ${state.resultPreview}",
                        fontSize = 20.sp,
                        color = DgEmerald,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.testTag("calculator_result_preview")
                    )
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Mode Switcher (Basic vs Scientific)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = state.mode == CalculatorMode.BASIC,
                onClick = { if (state.mode != CalculatorMode.BASIC) viewModel.toggleMode() },
                label = { Text("Basic Mode", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DgNavyPrimary,
                    selectedLabelColor = Color.White
                )
            )

            FilterChip(
                selected = state.mode == CalculatorMode.SCIENTIFIC,
                onClick = { if (state.mode != CalculatorMode.SCIENTIFIC) viewModel.toggleMode() },
                label = { Text("Scientific / Advanced", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DgNavyPrimary,
                    selectedLabelColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Keypad Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Scientific Functions Panel (When enabled)
            AnimatedVisibility(visible = state.mode == CalculatorMode.SCIENTIFIC) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SciButton(text = "sin", onClick = { viewModel.onApplyScientificFunction("sin") }, modifier = Modifier.weight(1f))
                        SciButton(text = "cos", onClick = { viewModel.onApplyScientificFunction("cos") }, modifier = Modifier.weight(1f))
                        SciButton(text = "tan", onClick = { viewModel.onApplyScientificFunction("tan") }, modifier = Modifier.weight(1f))
                        SciButton(text = "log", onClick = { viewModel.onApplyScientificFunction("log") }, modifier = Modifier.weight(1f))
                        SciButton(text = "ln", onClick = { viewModel.onApplyScientificFunction("ln") }, modifier = Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SciButton(text = "x²", onClick = { viewModel.onApplyScientificFunction("x²") }, modifier = Modifier.weight(1f))
                        SciButton(text = "√", onClick = { viewModel.onApplyScientificFunction("√") }, modifier = Modifier.weight(1f))
                        SciButton(text = "x^y", onClick = { viewModel.onApplyScientificFunction("^") }, modifier = Modifier.weight(1f))
                        SciButton(text = "π", onClick = { viewModel.onApplyScientificFunction("π") }, modifier = Modifier.weight(1f))
                        SciButton(text = "e", onClick = { viewModel.onApplyScientificFunction("e") }, modifier = Modifier.weight(1f))
                    }
                }
            }

            // Standard 4 x 5 Calculator Keypad
            // Row 1: AC, (, ), ÷
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcKey(text = "AC", color = Color(0xFFFEE2E2), textColor = Color(0xFFDC2626), modifier = Modifier.weight(1f), testTag = "key_ac") { viewModel.onClearAll() }
                CalcKey(text = "(", color = Color(0xFFE2E8F0), textColor = DgNavyDark, modifier = Modifier.weight(1f)) { viewModel.onTokenInput("(") }
                CalcKey(text = ")", color = Color(0xFFE2E8F0), textColor = DgNavyDark, modifier = Modifier.weight(1f)) { viewModel.onTokenInput(")") }
                CalcKey(text = "÷", color = DgNavyPrimary, textColor = Color.White, modifier = Modifier.weight(1f), testTag = "key_div") { viewModel.onTokenInput("÷") }
            }

            // Row 2: 7, 8, 9, ×
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcKey(text = "7", modifier = Modifier.weight(1f), testTag = "key_7") { viewModel.onTokenInput("7") }
                CalcKey(text = "8", modifier = Modifier.weight(1f), testTag = "key_8") { viewModel.onTokenInput("8") }
                CalcKey(text = "9", modifier = Modifier.weight(1f), testTag = "key_9") { viewModel.onTokenInput("9") }
                CalcKey(text = "×", color = DgNavyPrimary, textColor = Color.White, modifier = Modifier.weight(1f), testTag = "key_mul") { viewModel.onTokenInput("×") }
            }

            // Row 3: 4, 5, 6, -
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcKey(text = "4", modifier = Modifier.weight(1f), testTag = "key_4") { viewModel.onTokenInput("4") }
                CalcKey(text = "5", modifier = Modifier.weight(1f), testTag = "key_5") { viewModel.onTokenInput("5") }
                CalcKey(text = "6", modifier = Modifier.weight(1f), testTag = "key_6") { viewModel.onTokenInput("6") }
                CalcKey(text = "-", color = DgNavyPrimary, textColor = Color.White, modifier = Modifier.weight(1f), testTag = "key_sub") { viewModel.onTokenInput("-") }
            }

            // Row 4: 1, 2, 3, +
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcKey(text = "1", modifier = Modifier.weight(1f), testTag = "key_1") { viewModel.onTokenInput("1") }
                CalcKey(text = "2", modifier = Modifier.weight(1f), testTag = "key_2") { viewModel.onTokenInput("2") }
                CalcKey(text = "3", modifier = Modifier.weight(1f), testTag = "key_3") { viewModel.onTokenInput("3") }
                CalcKey(text = "+", color = DgNavyPrimary, textColor = Color.White, modifier = Modifier.weight(1f), testTag = "key_add") { viewModel.onTokenInput("+") }
            }

            // Row 5: ⌫, 0, ., =
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE2E8F0))
                        .clickable { viewModel.onBackspace() }
                        .testTag("key_backspace"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Backspace,
                        contentDescription = "Backspace",
                        tint = DgNavyDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                CalcKey(text = "0", modifier = Modifier.weight(1f), testTag = "key_0") { viewModel.onTokenInput("0") }
                CalcKey(text = ".", modifier = Modifier.weight(1f), testTag = "key_dot") { viewModel.onTokenInput(".") }
                CalcKey(text = "=", color = DgEmerald, textColor = Color.White, modifier = Modifier.weight(1f), testTag = "key_equals") { viewModel.onCalculateEquals() }
            }

            Spacer(modifier = Modifier.height(6.dp))
        }
    }

    // Calculation History Bottom Sheet
    if (state.showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleHistorySheet(false) },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calculation History",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DgNavyDark
                    )

                    if (state.history.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearHistory() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear History",
                                tint = Color(0xFFEF4444)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (state.history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No calculations in history yet",
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.history) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.recallHistory(item) },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = item.expression,
                                        fontSize = 13.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = "= ${item.result}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DgNavyDark
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CalcKey(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    textColor: Color = DgNavyDark,
    testTag: String = "",
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun SciButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFEDE9FE))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF5B21B6)
        )
    }
}
