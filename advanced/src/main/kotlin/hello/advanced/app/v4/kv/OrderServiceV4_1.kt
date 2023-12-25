package hello.advanced.app.v4.kv

import hello.advanced.trace.logtrace.LogTrace
import hello.advanced.trace.template.AbstractTemplate
import hello.advanced.trace.template.TemplatePattern
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class OrderServiceV4_1(
    private val orderRepositoryV4_1: OrderRepositoryV4_1,
    trace: LogTrace,
) {
    private val template = TemplatePattern<Unit>(trace)

    fun orderItem(itemId: String) {
        template.execute(
            message = "OrderService.orderItem()",
            call = { orderRepositoryV4_1.save(itemId) }
        )
    }
}