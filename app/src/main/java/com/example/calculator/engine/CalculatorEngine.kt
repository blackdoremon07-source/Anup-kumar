package com.example.calculator.engine

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

object CalculatorEngine {

    private val decimalFormat = DecimalFormat("0.########", DecimalFormatSymbols(Locale.US))

    fun formatNumber(number: Double): String {
        if (number.isNaN()) return "Error: NaN"
        if (number.isInfinite()) return if (number > 0) "Infinity" else "-Infinity"
        val formatted = decimalFormat.format(number)
        return if (formatted == "-0") "0" else formatted
    }

    /**
     * Appends an input character or operator while preventing invalid syntax combinations.
     */
    fun appendToken(current: String, token: String): String {
        if (current == "0" && token in "0123456789") {
            return token
        }
        if (token == ".") {
            // Find last number segment
            val lastToken = current.takeLastWhile { it in "0123456789." }
            if (lastToken.contains(".")) return current
            if (current.isEmpty() || current.last() in "+-×÷^(") return current + "0."
            return current + "."
        }

        val isOp = token in listOf("+", "-", "×", "÷", "^", "%")
        if (isOp) {
            if (current.isEmpty()) {
                return if (token == "-") "-" else ""
            }
            val lastChar = current.last().toString()
            if (lastChar in listOf("+", "-", "×", "÷", "^")) {
                // Replace previous operator with the newly selected one
                return current.dropLast(1) + token
            }
            if (lastChar == "(") {
                return if (token == "-") current + "-" else current
            }
        }

        return current + token
    }

    /**
     * Evaluates a mathematical expression string.
     */
    fun evaluate(expressionStr: String): Result<Double> {
        if (expressionStr.isBlank()) return Result.failure(Exception("Empty expression"))

        var sanitized = expressionStr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", Math.PI.toString())
            .replace("e", Math.E.toString())

        // Auto-close missing parentheses
        val openCount = sanitized.count { it == '(' }
        val closeCount = sanitized.count { it == ')' }
        if (openCount > closeCount) {
            sanitized += ")".repeat(openCount - closeCount)
        }

        return try {
            val parser = MathParser(sanitized)
            val result = parser.parse()
            if (result.isInfinite() || result.isNaN()) {
                Result.failure(Exception("Math error: Division by zero or out of range"))
            } else {
                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Recursive descent expression parser.
     * Grammar:
     * Expression = Term (+|- Term)*
     * Term = Factor (*|/|% Factor)*
     * Factor = Power (^ Power)*
     * Power = Unary
     * Unary = + Unary | - Unary | Primary
     * Primary = Number | Function(Expression) | (Expression)
     */
    private class MathParser(private val input: String) {
        private var pos = -1
        private var ch = ' '

        private fun nextChar() {
            pos++
            ch = if (pos < input.length) input[pos] else '\u0000'
        }

        private fun eat(charToEat: Char): Boolean {
            while (ch == ' ') nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val value = parseExpression()
            if (pos < input.length) throw IllegalArgumentException("Unexpected: '$ch'")
            return value
        }

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+') -> x += parseTerm()
                    eat('-') -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*') -> x *= parseFactor()
                    eat('/') -> {
                        val denom = parseFactor()
                        if (denom == 0.0) throw ArithmeticException("Division by zero")
                        x /= denom
                    }
                    eat('%') -> {
                        val denom = parseFactor()
                        if (denom == 0.0) throw ArithmeticException("Division by zero")
                        x %= denom
                    }
                    else -> return x
                }
            }
        }

        private fun parseFactor(): Double {
            var x = parsePower()
            if (eat('^')) {
                val exponent = parseFactor()
                x = x.pow(exponent)
            }
            return x
        }

        private fun parsePower(): Double {
            while (ch == ' ') nextChar()
            if (eat('+')) return parsePower()
            if (eat('-')) return -parsePower()

            var x: Double
            val startPos = pos
            if (eat('(')) {
                x = parseExpression()
                if (!eat(')')) throw IllegalArgumentException("Missing closing parenthesis")
            } else if (ch in '0'..'9' || ch == '.') {
                while (ch in '0'..'9' || ch == '.') nextChar()
                x = input.substring(startPos, pos).toDouble()
            } else if (ch in 'a'..'z' || ch == '√') {
                while (ch in 'a'..'z' || ch == '√') nextChar()
                val func = input.substring(startPos, pos)
                if (eat('(')) {
                    val arg = parseExpression()
                    if (!eat(')')) throw IllegalArgumentException("Missing closing parenthesis")
                    x = applyFunction(func, arg)
                } else if (func == "√") {
                    x = sqrt(parsePower())
                } else {
                    throw IllegalArgumentException("Unknown token: $func")
                }
            } else {
                throw IllegalArgumentException("Unexpected character: '$ch'")
            }

            return x
        }

        private fun applyFunction(func: String, arg: Double): Double {
            return when (func) {
                "sqrt", "√" -> {
                    if (arg < 0) throw ArithmeticException("Square root of negative number")
                    sqrt(arg)
                }
                "sin" -> sin(Math.toRadians(arg))
                "cos" -> cos(Math.toRadians(arg))
                "tan" -> tan(Math.toRadians(arg))
                "log" -> {
                    if (arg <= 0) throw ArithmeticException("Log of non-positive number")
                    log10(arg)
                }
                "ln" -> {
                    if (arg <= 0) throw ArithmeticException("Ln of non-positive number")
                    ln(arg)
                }
                else -> throw IllegalArgumentException("Unknown function: $func")
            }
        }
    }
}
