package com.example.calculator.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.calculator.engine.CalculatorEngine
import com.example.calculator.model.CalculationHistoryItem
import com.example.calculator.model.CalculatorMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CalculatorUiState(
    val expression: String = "",
    val resultPreview: String = "",
    val errorMessage: String? = null,
    val mode: CalculatorMode = CalculatorMode.BASIC,
    val history: List<CalculationHistoryItem> = emptyList(),
    val showHistorySheet: Boolean = false
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onTokenInput(token: String) {
        _uiState.update { state ->
            val newExpr = CalculatorEngine.appendToken(state.expression, token)
            val preview = evaluatePreview(newExpr)
            state.copy(
                expression = newExpr,
                resultPreview = preview,
                errorMessage = null
            )
        }
    }

    fun onClearAll() {
        _uiState.update {
            it.copy(
                expression = "",
                resultPreview = "",
                errorMessage = null
            )
        }
    }

    fun onBackspace() {
        _uiState.update { state ->
            if (state.expression.isNotEmpty()) {
                val newExpr = state.expression.dropLast(1)
                val preview = evaluatePreview(newExpr)
                state.copy(
                    expression = newExpr,
                    resultPreview = preview,
                    errorMessage = null
                )
            } else {
                state
            }
        }
    }

    fun onToggleSign() {
        _uiState.update { state ->
            if (state.expression.isEmpty()) {
                state.copy(expression = "-")
            } else if (state.expression.startsWith("-")) {
                val newExpr = state.expression.removePrefix("-")
                state.copy(expression = newExpr, resultPreview = evaluatePreview(newExpr))
            } else {
                val newExpr = "-(${state.expression})"
                state.copy(expression = newExpr, resultPreview = evaluatePreview(newExpr))
            }
        }
    }

    fun onCalculateEquals() {
        val state = _uiState.value
        if (state.expression.isBlank()) return

        val evalResult = CalculatorEngine.evaluate(state.expression)
        evalResult.onSuccess { value ->
            val formatted = CalculatorEngine.formatNumber(value)
            val historyItem = CalculationHistoryItem(
                expression = state.expression,
                result = formatted
            )
            _uiState.update {
                it.copy(
                    expression = formatted,
                    resultPreview = "",
                    errorMessage = null,
                    history = listOf(historyItem) + it.history.take(49)
                )
            }
        }.onFailure { e ->
            _uiState.update {
                it.copy(
                    errorMessage = e.message ?: "Invalid Expression"
                )
            }
        }
    }

    fun onApplyScientificFunction(functionName: String) {
        _uiState.update { state ->
            val newExpr = when (functionName) {
                "x²" -> if (state.expression.isNotEmpty()) "(${state.expression})^2" else ""
                "√" -> if (state.expression.isNotEmpty()) "√(${state.expression})" else "√("
                "1/x" -> if (state.expression.isNotEmpty()) "1/(${state.expression})" else "1/"
                "sin" -> "${state.expression}sin("
                "cos" -> "${state.expression}cos("
                "tan" -> "${state.expression}tan("
                "log" -> "${state.expression}log("
                "ln" -> "${state.expression}ln("
                "π" -> CalculatorEngine.appendToken(state.expression, "π")
                "e" -> CalculatorEngine.appendToken(state.expression, "e")
                "^" -> CalculatorEngine.appendToken(state.expression, "^")
                "%" -> CalculatorEngine.appendToken(state.expression, "%")
                else -> state.expression
            }
            state.copy(
                expression = newExpr,
                resultPreview = evaluatePreview(newExpr),
                errorMessage = null
            )
        }
    }

    fun toggleMode() {
        _uiState.update {
            it.copy(
                mode = if (it.mode == CalculatorMode.BASIC) CalculatorMode.SCIENTIFIC else CalculatorMode.BASIC
            )
        }
    }

    fun toggleHistorySheet(show: Boolean) {
        _uiState.update { it.copy(showHistorySheet = show) }
    }

    fun recallHistory(item: CalculationHistoryItem) {
        _uiState.update {
            it.copy(
                expression = item.result,
                resultPreview = "",
                errorMessage = null,
                showHistorySheet = false
            )
        }
    }

    fun clearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
    }

    private fun evaluatePreview(expr: String): String {
        if (expr.isBlank()) return ""
        val res = CalculatorEngine.evaluate(expr)
        return res.getOrNull()?.let { CalculatorEngine.formatNumber(it) } ?: ""
    }
}
