package com.example.unitconverter.model

data class UnitDefinition(
    val id: String,
    val name: String,
    val symbol: String,
    val factorToBase: Double = 1.0 // Multiplied by value to convert to base unit
)

enum class UnitCategory(
    val title: String,
    val baseUnitName: String,
    val iconName: String
) {
    LENGTH("Length", "Meter", "Straighten"),
    WEIGHT("Weight", "Kilogram", "FitnessCenter"),
    TEMPERATURE("Temperature", "Celsius", "Thermostat"),
    AREA("Area", "Square Meter", "CropLandscape"),
    VOLUME("Volume", "Liter", "LocalGasStation"),
    TIME("Time", "Second", "AvTimer"),
    SPEED("Speed", "Meter per second", "Speed"),
    DATA_STORAGE("Data Storage", "Megabyte", "Memory"),
    PRESSURE("Pressure", "Pascal", "Compress"),
    ENERGY("Energy", "Joule", "Bolt")
}

data class ConversionResult(
    val inputValue: Double,
    val fromUnit: UnitDefinition,
    val toUnit: UnitDefinition,
    val convertedValue: Double,
    val formulaText: String,
    val allCategoryConversions: List<Pair<UnitDefinition, Double>>
)
