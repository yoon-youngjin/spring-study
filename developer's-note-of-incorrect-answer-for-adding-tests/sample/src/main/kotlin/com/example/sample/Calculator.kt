package com.example.sample

class Calculator {
    fun calculate(num1: Long, op: String, num2: Long): Long {
        return when (op) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            "/" -> num1 / num2
            else -> throw InvalidOperationException()
        }
    }
}