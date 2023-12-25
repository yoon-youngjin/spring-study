package hello.advanced.app.v5

import hello.advanced.trace.callback.TraceTemplate
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class OrderControllerV5(
    private val orderServiceV5: OrderServiceV5,
    private val template: TraceTemplate,
) {

    @GetMapping("/v5/request")
    fun request(itemId: String): String {
        return template.execute("OrderController.request()") {
            orderServiceV5.orderItem(itemId)
            "ok"
        }
    }
}
