package com.example.sample

import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SampleApplication

fun main(args: Array<String>) {
    val request = CalculationRequestReader().read()

    println(Calculator().calculate(
        num1 = request.num1,
        op = request.operator,
        num2 = request.num2)
    )
}

