package hello.proxy.jdkdynamic.code

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(TimeInvocationHandler::class.java.name)

class TimeInvocationHandler(
    private val target: Any,
) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
        logger.info("TimeProxy 실행")
        val startTime = System.currentTimeMillis()

        val result = method(target)

        val endTime = System.currentTimeMillis()
        logger.info("TimeProxy 종료 resultTime=${endTime - startTime}ms")
        return result
    }
}
