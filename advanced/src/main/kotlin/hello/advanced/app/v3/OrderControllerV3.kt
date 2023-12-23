package hello.advanced.app.v3

import hello.advanced.trace.TraceStatus
import hello.advanced.trace.logtrace.LogTrace
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class OrderControllerV3(
    private val orderServiceV3: OrderServiceV3,
    private val trace: LogTrace,
) {

    @GetMapping("/v3/request")
    fun request(itemId: String): String {

        var status: TraceStatus? = null
        try {
            status = trace.begin("OrderController.request()")

            orderServiceV3.orderItem(itemId)

            trace.end(status)
        } catch (e: Exception) {
            trace.exception(status, e)
            throw e
        }

        return "ok"
    }
}