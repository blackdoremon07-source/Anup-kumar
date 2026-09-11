package com.example.calculator.model

data class CalculationHistoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class CalculatorMode {
    BASIC,
    SCIENTIFIC
}
