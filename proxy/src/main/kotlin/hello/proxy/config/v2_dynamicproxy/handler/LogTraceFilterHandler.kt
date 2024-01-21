package hello.proxy.config.v2_dynamicproxy.handler

import hello.advanced.trace.TraceStatus
import hello.proxy.trace.logtrace.LogTrace
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import org.springframework.util.PatternMatchUtils

class LogTraceFilterHandler(
    private val target: Any,
    private val logTrace: LogTrace,
    private val patterns: Array<String>,
) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {

        // 메서드 이름 필터
        if (!PatternMatchUtils.simpleMatch(patterns, method.name)) {
            return invoke(args, method)
        }

        var status: TraceStatus? = null
        return try {
            val message = "${method.declaringClass.getSimpleName()}.${method.name}()"
            status = logTrace.begin(message)
            // target 호출
            val result = invoke(args, method)
            logTrace.end(status)
            result
        } catch (e: Exception) {
            logTrace.exception(status, e)
            throw e
        }
    }

    private fun invoke(args: Array<out Any>?, method: Method): Any? =
        if (args == null) {
            method.invoke(target)
        } else {
            method.invoke(target, *args)
        }
}