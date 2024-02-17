package hello.aop.exam.aop

import hello.aop.exam.annotation.Retry
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(RetryAspect::class.java)

@Aspect
class RetryAspect {

    @Around("@annotation(retry)")
    fun doRetry(joinPoint: ProceedingJoinPoint, retry: Retry): Any? {
        logger.info("[retry] ${joinPoint.signature}, retry=$retry")

        val maxRetry = retry.value
        var exceptionHolder: Exception? = null

        for (retryCnt in 1..maxRetry) {
            logger.info("[retry] try count=$retryCnt/$maxRetry")
            try {
                return joinPoint.proceed()
            } catch (e: Exception) {
                exceptionHolder = e
            }
        }
        throw exceptionHolder!!
    }
}