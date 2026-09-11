package com.example.unitconverter.engine

import com.example.unitconverter.model.ConversionResult
import com.example.unitconverter.model.UnitCategory
import com.example.unitconverter.model.UnitDefinition
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object UnitConverterEngine {

    private val decimalFormat = DecimalFormat("0.######", DecimalFormatSymbols(Locale.US))

    fun formatNumber(value: Double): String {
        if (value.isNaN()) return "0"
        if (value.isInfinite()) return "Infinity"
        if (Math.abs(value) < 1e-6 && value != 0.0) {
            return String.format(Locale.US, "%.4e", value)
        }
        val formatted = decimalFormat.format(value)
        return if (formatted == "-0") "0" else formatted
    }

    fun getUnitsForCategory(category: UnitCategory): List<UnitDefinition> = when (category) {
        UnitCategory.LENGTH -> listOf(
            UnitDefinition("mm", "Millimeter", "mm", 0.001),
            UnitDefinition("cm", "Centimeter", "cm", 0.01),
            UnitDefinition("m", "Meter (Base)", "m", 1.0),
            UnitDefinition("km", "Kilometer", "km", 1000.0),
            UnitDefinition("in", "Inch", "in", 0.0254),
            UnitDefinition("ft", "Foot", "ft", 0.3048),
            UnitDefinition("yd", "Yard", "yd", 0.9144),
            UnitDefinition("mi", "Mile", "mi", 1609.344)
        )

        UnitCategory.WEIGHT -> listOf(
            UnitDefinition("mg", "Milligram", "mg", 0.000001),
            UnitDefinition("g", "Gram", "g", 0.001),
            UnitDefinition("kg", "Kilogram (Base)", "kg", 1.0),
            UnitDefinition("quintal", "Quintal", "q", 100.0),
            UnitDefinition("ton", "Metric Ton", "t", 1000.0),
            UnitDefinition("oz", "Ounce", "oz", 0.028349523125),
            UnitDefinition("lb", "Pound", "lb", 0.45359237)
        )

        UnitCategory.TEMPERATURE -> listOf(
            UnitDefinition("c", "Celsius", "°C", 1.0),
            UnitDefinition("f", "Fahrenheit", "°F", 1.0),
            UnitDefinition("k", "Kelvin", "K", 1.0)
        )

        UnitCategory.AREA -> listOf(
            UnitDefinition("sq_mm", "Square Millimeter", "mm²", 0.000001),
            UnitDefinition("sq_cm", "Square Centimeter", "cm²", 0.0001),
            UnitDefinition("sq_m", "Square Meter (Base)", "m²", 1.0),
            UnitDefinition("sq_km", "Square Kilometer", "km²", 1000000.0),
            UnitDefinition("sq_in", "Square Inch", "in²", 0.00064516),
            UnitDefinition("sq_ft", "Square Foot", "ft²", 0.09290304),
            UnitDefinition("sq_yd", "Square Yard", "yd²", 0.83612736),
            UnitDefinition("acre", "Acre", "ac", 4046.8564224),
            UnitDefinition("hectare", "Hectare", "ha", 10000.0)
        )

        UnitCategory.VOLUME -> listOf(
            UnitDefinition("ml", "Milliliter", "mL", 0.001),
            UnitDefinition("l", "Liter (Base)", "L", 1.0),
            UnitDefinition("cu_m", "Cubic Meter", "m³", 1000.0),
            UnitDefinition("cu_cm", "Cubic Centimeter", "cm³", 0.001),
            UnitDefinition("cu_in", "Cubic Inch", "in³", 0.016387064),
            UnitDefinition("cu_ft", "Cubic Foot", "ft³", 28.316846592),
            UnitDefinition("gal", "Gallon (US)", "gal", 3.785411784),
            UnitDefinition("fl_oz", "Fluid Ounce (US)", "fl oz", 0.0295735295625)
        )

        UnitCategory.TIME -> listOf(
            UnitDefinition("ms", "Millisecond", "ms", 0.001),
            UnitDefinition("s", "Second (Base)", "s", 1.0),
            UnitDefinition("min", "Minute", "min", 60.0),
            UnitDefinition("h", "Hour", "hr", 3600.0),
            UnitDefinition("d", "Day", "d", 86400.0),
            UnitDefinition("wk", "Week", "wk", 604800.0),
            UnitDefinition("mo", "Month (avg 30.44d)", "mo", 2629800.0),
            UnitDefinition("yr", "Year (365d)", "yr", 31536000.0)
        )

        UnitCategory.SPEED -> listOf(
            UnitDefinition("mps", "Meter per Second (Base)", "m/s", 1.0),
            UnitDefinition("kmh", "Kilometer per Hour", "km/h", 0.277777778),
            UnitDefinition("mph", "Mile per Hour", "mph", 0.44704),
            UnitDefinition("knot", "Knot", "kn", 0.514444444),
            UnitDefinition("fps", "Foot per Second", "ft/s", 0.3048)
        )

        UnitCategory.DATA_STORAGE -> listOf(
            UnitDefinition("b", "Byte", "B", 0.000001),
            UnitDefinition("kb", "Kilobyte (1024 B)", "KB", 0.001024),
            UnitDefinition("mb", "Megabyte (Base)", "MB", 1.0),
            UnitDefinition("gb", "Gigabyte (1024 MB)", "GB", 1024.0),
            UnitDefinition("tb", "Terabyte (1024 GB)", "TB", 1048576.0),
            UnitDefinition("pb", "Petabyte (1024 TB)", "PB", 1073741824.0)
        )

        UnitCategory.PRESSURE -> listOf(
            UnitDefinition("pa", "Pascal (Base)", "Pa", 1.0),
            UnitDefinition("kpa", "Kilopascal", "kPa", 1000.0),
            UnitDefinition("bar", "Bar", "bar", 100000.0),
            UnitDefinition("psi", "Pound per Sq Inch", "psi", 6894.75729),
            UnitDefinition("atm", "Standard Atmosphere", "atm", 101325.0),
            UnitDefinition("mmhg", "Millimeter of Mercury", "mmHg", 133.322)
        )

        UnitCategory.ENERGY -> listOf(
            UnitDefinition("j", "Joule (Base)", "J", 1.0),
            UnitDefinition("kj", "Kilojoule", "kJ", 1000.0),
            UnitDefinition("cal", "Calorie", "cal", 4.184),
            UnitDefinition("kcal", "Kilocalorie (Food)", "kcal", 4184.0),
            UnitDefinition("wh", "Watt-hour", "Wh", 3600.0),
            UnitDefinition("kwh", "Kilowatt-hour", "kWh", 3600000.0),
            UnitDefinition("ev", "Electronvolt", "eV", 1.602176634e-19)
        )
    }

    fun convert(
        value: Double,
        category: UnitCategory,
        fromUnit: UnitDefinition,
        toUnit: UnitDefinition
    ): ConversionResult {
        val converted = if (category == UnitCategory.TEMPERATURE) {
            convertTemperature(value, fromUnit.id, toUnit.id)
        } else {
            val baseValue = value * fromUnit.factorToBase
            baseValue / toUnit.factorToBase
        }

        val formula = if (category == UnitCategory.TEMPERATURE) {
            getTemperatureFormula(value, fromUnit.id, toUnit.id)
        } else {
            val ratio = fromUnit.factorToBase / toUnit.factorToBase
            "Multiply ${fromUnit.symbol} by ${formatNumber(ratio)} to get ${toUnit.symbol}"
        }

        val allConversions = getUnitsForCategory(category).map { targetUnit ->
            val targetVal = if (category == UnitCategory.TEMPERATURE) {
                convertTemperature(value, fromUnit.id, targetUnit.id)
            } else {
                (value * fromUnit.factorToBase) / targetUnit.factorToBase
            }
            Pair(targetUnit, targetVal)
        }

        return ConversionResult(
            inputValue = value,
            fromUnit = fromUnit,
            toUnit = toUnit,
            convertedValue = converted,
            formulaText = formula,
            allCategoryConversions = allConversions
        )
    }

    private fun convertTemperature(value: Double, fromId: String, toId: String): Double {
        val inCelsius = when (fromId) {
            "c" -> value
            "f" -> (value - 32.0) * (5.0 / 9.0)
            "k" -> value - 273.15
            else -> value
        }

        return when (toId) {
            "c" -> inCelsius
            "f" -> (inCelsius * (9.0 / 5.0)) + 32.0
            "k" -> inCelsius + 273.15
            else -> inCelsius
        }
    }

    private fun getTemperatureFormula(value: Double, fromId: String, toId: String): String {
        return when {
            fromId == "c" && toId == "f" -> "(${formatNumber(value)}°C × 9/5) + 32"
            fromId == "f" && toId == "c" -> "(${formatNumber(value)}°F - 32) × 5/9"
            fromId == "c" && toId == "k" -> "${formatNumber(value)}°C + 273.15"
            fromId == "k" && toId == "c" -> "${formatNumber(value)}K - 273.15"
            fromId == "f" && toId == "k" -> "(${formatNumber(value)}°F - 32) × 5/9 + 273.15"
            fromId == "k" && toId == "f" -> "(${formatNumber(value)}K - 273.15) × 9/5 + 32"
            else -> "Same temperature scale"
        }
    }
}
