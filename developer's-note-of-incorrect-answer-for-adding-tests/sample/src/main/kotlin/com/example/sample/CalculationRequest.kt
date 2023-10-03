package com.example.sample

data class CalculationRequest(val parts: List<String>) {

    init {
        validateRequest(parts)
    }

    private fun validateRequest(parts: List<String>) {
        if (parts.size != 3) {
            throw BadRequestException()
        }
        if (parts[1].length != 1 || isInvalidOperation(parts[1])) {
            throw InvalidOperationException()
        }
    }

    val num1: Long = parts[0].toLong()
    val num2: Long = parts[2].toLong()
    val operator: String = parts[1]

    private fun isInvalidOperation(operator: String) =
        operator != "+" && operator != "-" && operator != "*" && operator != "/"
}