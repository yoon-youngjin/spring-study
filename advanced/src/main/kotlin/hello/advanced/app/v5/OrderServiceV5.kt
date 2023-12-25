package hello.advanced.app.v5

import hello.advanced.trace.callback.TraceTemplate
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class OrderServiceV5(
    private val orderRepositoryV5: OrderRepositoryV5,
    private val template: TraceTemplate,
) {

    fun orderItem(itemId: String) {
        template.execute("OrderService.orderItem()") {
            orderRepositoryV5.save(itemId)
        }
    }
}
