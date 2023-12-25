package hello.advanced.app.v4

import hello.advanced.trace.TraceStatus
import hello.advanced.trace.logtrace.LogTrace
import hello.advanced.trace.template.AbstractTemplate
import hello.advanced.trace.template.TemplatePattern
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class OrderControllerV4(
    private val orderServiceV4: OrderServiceV4,
    private val trace: LogTrace,
) {

    @GetMapping("/v4/request")
    fun request(itemId: String): String {
        val template = object : AbstractTemplate<String>(trace) {
            override fun call(): String {
                orderServiceV4.orderItem(itemId)
                return "ok"
            }
        }
        return template.execute("OrderController.request()")
    }
}