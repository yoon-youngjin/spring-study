package hello.proxy.cglib.code

import java.lang.reflect.Method
import org.slf4j.LoggerFactory
import org.springframework.cglib.proxy.MethodInterceptor
import org.springframework.cglib.proxy.MethodProxy

private val logger = LoggerFactory.getLogger(TimeMethodInterceptor::class.java.name)

class TimeMethodInterceptor(
    private val target: Any,
) : MethodInterceptor {
    override fun intercept(obj: Any?, method: Method?, args: Array<out Any>?, proxy: MethodProxy): Any? {
        logger.info("TimeProxy 실행")
        val startTime = System.currentTimeMillis()

        val result = proxy(target, args)

        val endTime = System.currentTimeMillis()
        logger.info("TimeProxy 종료 resultTime=${endTime - startTime}ms")
        return result
    }
}