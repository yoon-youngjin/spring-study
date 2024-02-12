package hello.aop

import hello.aop.order.OrderRepository
import hello.aop.order.OrderService
import hello.aop.order.aop.AspectV5Order
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.aop.support.AopUtils
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestConstructor

private val logger = LoggerFactory.getLogger(AopTest::class.java)

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(AspectV5Order.LogAspect::class, AspectV5Order.TxAspect::class)
class AopTest(
    private val orderService: OrderService,
    private val orderRepository: OrderRepository,
) {

    @Test
    fun aopInfo() {
        logger.info("isAopProxy, orderService=${AopUtils.isAopProxy(orderService)}")
        logger.info("isAopProxy, orderRepository=${AopUtils.isAopProxy(orderRepository)}")
    }

    @Test
    fun success() {
        orderService.orderItem("itemA")
    }

    @Test
    fun exception() {
        Assertions.assertThatThrownBy {
            orderService.orderItem("ex")
        }.isInstanceOf(IllegalStateException::class.java)
    }
}