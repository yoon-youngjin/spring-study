package hello.aop.order.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(AspectV2::class.java)

@Aspect
class AspectV2 {
    @Pointcut("execution(* hello.aop.order..*(..))")
    fun allOrder() {}

    @Around("allOrder()") // 포인트컷
    fun doLog(joinPoint: ProceedingJoinPoint): Any? { // 어드바이스
        logger.info("[log] ${joinPoint.signature}")
        return joinPoint.proceed()
    }
}