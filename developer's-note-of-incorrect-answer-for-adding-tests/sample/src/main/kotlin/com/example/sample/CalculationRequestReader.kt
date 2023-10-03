package com.example.sample

import java.util.Scanner

class CalculationRequestReader {

    fun read(): CalculationRequest {
        val scanner = Scanner(System.`in`)
        println("Enter two numbers and an operator (e.g. 1 + 2) ")
        val result = scanner.nextLine()
        val parts = result.split(" ")

        return CalculationRequest(parts)
    }
}