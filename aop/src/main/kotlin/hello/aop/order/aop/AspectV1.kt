package hello.aop.order.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(AspectV1::class.java)

@Aspect
class AspectV1 {

    @Around("execution(* hello.aop.order..*(..))") // 포인트컷
    fun doLog(joinPoint: ProceedingJoinPoint): Any? { // 어드바이스
        logger.info("[log] ${joinPoint.signature}")
        return joinPoint.proceed()
    }
}