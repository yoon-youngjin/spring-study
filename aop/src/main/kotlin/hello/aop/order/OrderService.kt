package hello.aop.order

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

private val logger = LoggerFactory.getLogger(OrderService::class.java)

@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {
    fun orderItem(itemId: String) {
        logger.info("[orderService] 실행")
        orderRepository.save(itemId)
    }
}