package com.example.sample

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class CalculatorTest {

    @Test
    fun `덧셈 연산이 정상적으로 동작할 수 있다`() {
        // given
        val num1 = 2L
        val op = "+"
        val num2 = 3L
        val calculator = Calculator()

        // when
        val result = calculator.calculate(num1, op, num2)

        // then
        result shouldBe 5L
    }

    @Test
    fun `뺄셈 연산이 정상적으로 동작할 수 있다`() {
        // given
        val num1 = 3L
        val op = "-"
        val num2 = 2L
        val calculator = Calculator()

        // when
        val result = calculator.calculate(num1, op, num2)

        // then
        result shouldBe 1L
    }

    @Test
    fun `곱셈 연산이 정상적으로 동작할 수 있다`() {
        // given
        val num1 = 3L
        val op = "*"
        val num2 = 2L
        val calculator = Calculator()

        // when
        val result = calculator.calculate(num1, op, num2)

        // then
        result shouldBe 6L
    }

    @Test
    fun `나눗셈 연산이 정상적으로 동작할 수 있다`() {
        // given
        val num1 = 4L
        val op = "/"
        val num2 = 2L
        val calculator = Calculator()

        // when
        val result = calculator.calculate(num1, op, num2)

        // then
        result shouldBe 2L
    }

    @Test
    fun `예상하지 못한 연산자가 입력으로 들어오면 InvalidOperationException이 발생한다`() {
        // given
        val num1 = 4L
        val op = "#"
        val num2 = 2L
        val calculator = Calculator()

        // when, then
        val exception = shouldThrow<InvalidOperationException> {
            calculator.calculate(num1, op, num2)
        }
        exception.message shouldBe "Invalid operator, you need to choose one of (+, -, *, /)"
    }
}