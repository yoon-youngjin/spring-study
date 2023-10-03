package com.example.sample

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CalculationRequestTest {

    @Test
    fun `유효한 숫자를 파싱할 수 있다`() {
        // given
        val parts = listOf("2", "+", "3")

        // when
        val request = CalculationRequest(parts)

        // then
        assertSoftly(request) {
            it.num1 shouldBe 2
            it.num2 shouldBe 3
            it.operator shouldBe "+"
        }
    }

    @Test
    fun `세자리 숫자가 넘어가는 유효한 숫자를 파싱할 수 있다`() {
        // given
        val parts = listOf("232", "+", "123")

        // when
        val request = CalculationRequest(parts)

        // then
        assertSoftly(request) {
            it.num1 shouldBe 232
            it.num2 shouldBe 123
            it.operator shouldBe "+"
        }
    }

    @Test
    fun `유효한 길이의 숫자가 들어오지 않으면 에러를 던진다`() {
        // given
        val parts = listOf("232", "+")

        // when, then
        val exception = shouldThrow<BadRequestException> {
            CalculationRequest(parts)
        }
        exception.message shouldBe "Invalid Input size, you must input 3 length"
    }

    @Test
    fun `유효하지 않은 연산자가 들어오면 에러를 던진다`() {
        // given
        val parts = listOf("232", "x", "123")

        // when, then
        val exception = shouldThrow<InvalidOperationException> {
            CalculationRequest(parts)
        }
        exception.message shouldBe "Invalid operator, you need to choose one of (+, -, *, /)"
    }

    @Test
    fun `유효하지 않은 길이의 연산자가 들어오면 에러를 던진다`() {
        // given
        val parts = listOf("232", "+=", "123")

        // when, then
        val exception = shouldThrow<InvalidOperationException> {
            CalculationRequest(parts)
        }
        exception.message shouldBe "Invalid operator, you need to choose one of (+, -, *, /)"
    }
}
