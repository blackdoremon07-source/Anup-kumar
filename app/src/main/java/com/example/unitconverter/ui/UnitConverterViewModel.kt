package com.example.unitconverter.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.unitconverter.engine.UnitConverterEngine
import com.example.unitconverter.model.ConversionResult
import com.example.unitconverter.model.UnitCategory
import com.example.unitconverter.model.UnitDefinition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UnitConverterUiState(
    val selectedCategory: UnitCategory = UnitCategory.LENGTH,
    val inputValueString: String = "1",
    val availableUnits: List<UnitDefinition> = emptyList(),
    val fromUnit: UnitDefinition? = null,
    val toUnit: UnitDefinition? = null,
    val conversionResult: ConversionResult? = null,
    val copiedNotice: String? = null
)

class UnitConverterViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UnitConverterUiState())
    val uiState: StateFlow<UnitConverterUiState> = _uiState.asStateFlow()

    init {
        selectCategory(UnitCategory.LENGTH)
    }

    fun selectCategory(category: UnitCategory) {
        val units = UnitConverterEngine.getUnitsForCategory(category)
        val defaultFrom = units.getOrNull(0)
        val defaultTo = units.getOrNull(1) ?: defaultFrom

        _uiState.update {
            it.copy(
                selectedCategory = category,
                availableUnits = units,
                fromUnit = defaultFrom,
                toUnit = defaultTo,
                copiedNotice = null
            )
        }
        recalculate()
    }

    fun updateInputString(input: String) {
        // Filter valid float input
        val clean = input.filter { it in "0123456789.-" }
        _uiState.update { it.copy(inputValueString = clean, copiedNotice = null) }
        recalculate()
    }

    fun setFromUnit(unit: UnitDefinition) {
        _uiState.update { it.copy(fromUnit = unit, copiedNotice = null) }
        recalculate()
    }

    fun setToUnit(unit: UnitDefinition) {
        _uiState.update { it.copy(toUnit = unit, copiedNotice = null) }
        recalculate()
    }

    fun swapUnits() {
        _uiState.update { state ->
            val prevFrom = state.fromUnit
            val prevTo = state.toUnit
            state.copy(fromUnit = prevTo, toUnit = prevFrom, copiedNotice = null)
        }
        recalculate()
    }

    fun copyResultToClipboard() {
        val result = _uiState.value.conversionResult ?: return
        val textToCopy = "${UnitConverterEngine.formatNumber(result.inputValue)} ${result.fromUnit.symbol} = ${UnitConverterEngine.formatNumber(result.convertedValue)} ${result.toUnit.symbol}"
        val cm = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Unit Conversion", textToCopy)
        cm.setPrimaryClip(clip)
        _uiState.update { it.copy(copiedNotice = "Copied to clipboard!") }
    }

    fun clearCopiedNotice() {
        _uiState.update { it.copy(copiedNotice = null) }
    }

    private fun recalculate() {
        val state = _uiState.value
        val num = state.inputValueString.toDoubleOrNull() ?: 0.0
        val from = state.fromUnit ?: return
        val to = state.toUnit ?: return

        val result = UnitConverterEngine.convert(
            value = num,
            category = state.selectedCategory,
            fromUnit = from,
            toUnit = to
        )

        _uiState.update { it.copy(conversionResult = result) }
    }
}
