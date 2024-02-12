package hello.aop.order

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

private val logger = LoggerFactory.getLogger(OrderRepository::class.java)

@Repository
class OrderRepository {

    fun save(itemId: String): String {
        logger.info("[orderRepository] 실행")
        if (itemId == "ex") {
            throw IllegalStateException("예외 발생!")
        }
        return "ok"
    }
}