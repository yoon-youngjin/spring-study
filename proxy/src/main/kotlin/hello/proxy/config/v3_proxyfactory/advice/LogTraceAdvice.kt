package hello.proxy.config.v3_proxyfactory.advice

import hello.advanced.trace.TraceStatus
import hello.proxy.trace.logtrace.LogTrace
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation

class LogTraceAdvice(
    private val logTrace: LogTrace,
) : MethodInterceptor {
    override fun invoke(invocation: MethodInvocation): Any? {
        var status: TraceStatus? = null
        return try {
            val message = "${invocation.method.declaringClass.getSimpleName()}.${invocation.method.name}()"
            status = logTrace.begin(message)
            // target 호출
            val result = invocation.proceed()
            logTrace.end(status)
            result
        } catch (e: Exception) {
            logTrace.exception(status, e)
            throw e
        }
    }
}