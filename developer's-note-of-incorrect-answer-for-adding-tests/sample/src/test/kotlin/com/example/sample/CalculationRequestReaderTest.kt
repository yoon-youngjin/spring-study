package com.example.sample

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import java.io.ByteArrayInputStream
import org.junit.jupiter.api.Test

internal class CalculationRequestReaderTest {

    @Test
    fun `System_in으로 데이터를 읽어들일 수 있다`() {
        // given
        val reader = CalculationRequestReader()

        // when
        System.setIn(ByteArrayInputStream("2 + 3".toByteArray()))
        val result = reader.read()

        // then
        assertSoftly(result) {
            it.num1 shouldBe 2L
            it.operator shouldBe "+"
            it.num2 shouldBe 3L
        }
    }
}