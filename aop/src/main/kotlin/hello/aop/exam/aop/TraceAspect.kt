package hello.aop.exam.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(TraceAspect::class.java)

@Aspect
class TraceAspect {
    @Around("@annotation(hello.aop.exam.annotation.Trace)")
    fun doTrace(joinPoint: ProceedingJoinPoint): Any? {
        val args = joinPoint.args
        logger.info("[trace] ${joinPoint.signature}, args=$args")
        return joinPoint.proceed()
    }
}