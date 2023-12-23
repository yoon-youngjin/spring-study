package hello.advanced.app.v0

import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class OrderServiceV0(
    private val orderRepositoryV0: OrderRepositoryV0,
) {

    fun orderItem(itemId: String) {
        orderRepositoryV0.save(itemId)
    }
}