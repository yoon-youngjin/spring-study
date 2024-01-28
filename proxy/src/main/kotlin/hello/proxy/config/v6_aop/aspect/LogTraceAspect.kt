package hello.proxy.config.v6_aop.aspect

import hello.advanced.trace.TraceStatus
import hello.proxy.trace.logtrace.LogTrace
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(LogTraceAspect::class.java)

@Aspect
class LogTraceAspect(
    private val logTrace: LogTrace,
) {

    @Around("execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))") // pointcut
    fun execute(joinPoint: ProceedingJoinPoint): Any? { // advice 로직
        var status: TraceStatus? = null

        logger.info("target=${joinPoint.target}") //실제 호출 대상
        logger.info("getArgs=${joinPoint.args}") //전달인자
        logger.info("getSignature=${joinPoint.signature}") //join point 시그니처

        return try {
            val message = joinPoint.signature.toShortString()
            status = logTrace.begin(message)
            // target 호출
            val result = joinPoint.proceed()
            logTrace.end(status)
            result
        } catch (e: Exception) {
            logTrace.exception(status, e)
            throw e
        }
    }
}