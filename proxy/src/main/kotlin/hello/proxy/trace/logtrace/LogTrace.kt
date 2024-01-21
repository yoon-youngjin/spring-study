package hello.proxy.trace.logtrace

import hello.advanced.trace.TraceStatus

interface LogTrace {
    fun begin(message: String): TraceStatus
    fun end(status: TraceStatus)
    fun exception(status: TraceStatus?, e: Exception)
}