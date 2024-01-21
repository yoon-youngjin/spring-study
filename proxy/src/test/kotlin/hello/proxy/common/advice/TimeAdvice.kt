package hello.proxy.common.advice

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.slf4j.LoggerFactory

class TimeAdvice : MethodInterceptor {
    private val logger = LoggerFactory.getLogger(TimeAdvice::class.java)
    override fun invoke(invocation: MethodInvocation): Any? {
        logger.info("TimeProxy 실행")
        val startTime = System.currentTimeMillis()

        val result = invocation.proceed()

        val endTime = System.currentTimeMillis()
        logger.info("TimeProxy 종료 resultTime=${endTime - startTime}ms")
        return result
    }
}