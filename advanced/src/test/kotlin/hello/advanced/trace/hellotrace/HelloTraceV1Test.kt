package hello.advanced.trace.hellotrace

import org.junit.jupiter.api.Test

internal class HelloTraceV1Test {

    private val trace = HelloTraceV1()

    @Test
    fun begin_end() {
        val status = trace.begin("hello")
        trace.end(status)
    }

    @Test
    fun begin_exception() {
        val status = trace.begin("hello")
        trace.exception(status, IllegalStateException())
    }
}