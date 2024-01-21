package hello.proxy.config.v2_dynamicproxy.handler

import hello.advanced.trace.TraceStatus
import hello.proxy.trace.logtrace.LogTrace
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class LogTraceBasicHandler(
    private val target: Any,
    private val logTrace: LogTrace,
) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        var status: TraceStatus? = null
        return try {
            val message = "${method.declaringClass.getSimpleName()}.${method.name}()"
            status = logTrace.begin(message)
            // target 호출
            val result = if (args == null) {
                method.invoke(target)
            } else {
                method.invoke(target, *args)
            }
            logTrace.end(status)
            result
        } catch (e: Exception) {
            logTrace.exception(status, e)
            throw e
        }
    }
}