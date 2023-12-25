package hello.advanced.app.v4

import hello.advanced.trace.TraceStatus
import hello.advanced.trace.logtrace.LogTrace
import hello.advanced.trace.template.AbstractTemplate
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class OrderServiceV4(
    private val orderRepositoryV4: OrderRepositoryV4,
    private val trace: LogTrace,
) {
    fun orderItem(itemId: String) {
        val template = object : AbstractTemplate<Unit>(trace) {
            override fun call() {
                orderRepositoryV4.save(itemId)
            }
        }
        template.execute("OrderService.orderItem()")
    }
}