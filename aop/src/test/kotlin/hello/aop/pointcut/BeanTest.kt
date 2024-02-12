package hello.aop.pointcut

import hello.aop.order.OrderService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

private val logger = LoggerFactory.getLogger(BeanTest::class.java)

@SpringBootTest
@Import(BeanTest.BeanAspect::class)
class BeanTest(
    @Autowired
    private val orderService: OrderService,
) {

    @Test
    fun success() {
        orderService.orderItem("itemA")
    }

    @Aspect
    class BeanAspect {
        @Around("bean(orderService) || bean(*Repository)")
        fun doLog(joinPoint: ProceedingJoinPoint): Any? {
            logger.info("[bean] ${joinPoint.signature}")
            return joinPoint.proceed()
        }
    }
}
