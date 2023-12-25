package hello.advanced.app.v4.kv

import hello.advanced.trace.logtrace.LogTrace
import hello.advanced.trace.template.TemplatePattern
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class OrderControllerV4_1(
    private val orderServiceV41: OrderServiceV4_1,
    trace: LogTrace,
) {
    private val template = TemplatePattern<String>(trace)

    @GetMapping("/v4-1/request")
    fun request(itemId: String): String {
        return template.execute(
            message = "OrderController.request()",
            call = {
                orderServiceV41.orderItem(itemId)
                "ok"
            }
        )
    }
}