package hello.aop.order.aop


import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Before
import org.slf4j.LoggerFactory


private val logger = LoggerFactory.getLogger(AspectV6Advice::class.java)

class AspectV6Advice {
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()") // 포인트컷
    fun doTransaction(joinPoint: ProceedingJoinPoint): Any? {
        return try {
            // @Before
            logger.info("[트랜잭션 시작] ${joinPoint.signature}")
            val result = joinPoint.proceed()
            // @AfterReturning
            logger.info("[트랜잭션 커밋] ${joinPoint.signature}")
            result
        } catch (e: Exception) {
            // @AfterThrowing
            logger.info("[트랜잭션 롤백] ${joinPoint.signature}")
            throw e
        } finally {
            // @After
            logger.info("[리소스 릴리즈] ${joinPoint.signature}")
        }
    }

    @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
    fun doBefore(joinPoint: JoinPoint) {
        logger.info("[before] ${joinPoint.signature}")
    }

    @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
    fun doAfterReturning(joinPoint: JoinPoint, result: Any?) {
        logger.info("[return] ${joinPoint.signature} return=$result")
    }

    @AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
    fun doAfterReturning(joinPoint: JoinPoint, ex: Exception) {
        logger.info("[ex] ${joinPoint.signature} message=${ex.message}")
    }

    @After(value = "hello.aop.order.aop.Pointcuts.orderAndService()")
    fun doAfter(joinPoint: JoinPoint) {
        logger.info("[after] ${joinPoint.signature}")
    }
}