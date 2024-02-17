package hello.aop.internalcall.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(CallLogAspect::class.java)

@Aspect
class CallLogAspect {

    @Around("execution(* hello.aop.internalcall..*.*(..))")
    fun doLog(joinPoint: ProceedingJoinPoint) {
        logger.info("aop=${joinPoint.signature}")
        joinPoint.proceed()
    }
}