package hello.advanced.trace.template

import hello.advanced.trace.TraceStatus
import hello.advanced.trace.logtrace.LogTrace

class TemplatePattern<T>(
    private val trace: LogTrace,
) {

    fun execute(message: String, call: () -> T): T {
        var status: TraceStatus? = null
        try {
            status = trace.begin(message)
            // 로직 호출
            val result = call()

            trace.end(status)
            return result
        } catch (e: Exception) {
            trace.exception(status, e)
            throw e
        }
    }
}
