package hello.aop.proxyvs.code

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(ProxyDIAspect::class.java)

@Aspect
class ProxyDIAspect {

    @Around("execution(* hello.aop..*.*(..))")
    fun doTrace(joinPoint: ProceedingJoinPoint) {
        logger.info("[proxyDIAdvice] ${joinPoint.signature}")
        joinPoint.proceed()
    }
}