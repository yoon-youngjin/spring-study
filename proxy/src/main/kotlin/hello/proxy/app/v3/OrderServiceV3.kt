package hello.proxy.app.v3

import org.springframework.stereotype.Service

@Service
class OrderServiceV3(
    private val orderRepository: OrderRepositoryV3,
) {
    fun orderItem(itemId: String) {
        orderRepository.save(itemId)
    }
}