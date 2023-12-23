package hello.advanced.app.v2

import hello.advanced.trace.TraceStatus
import hello.advanced.trace.hellotrace.HelloTraceV2
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class OrderControllerV2(
    private val orderServiceV2: OrderServiceV2,
    private val trace: HelloTraceV2,
) {

    @GetMapping("/v2/request")
    fun request(itemId: String): String {

        var status: TraceStatus? = null
        try {
            status = trace.begin("OrderController.request()")

            orderServiceV2.orderItem(itemId, status.traceId)

            trace.end(status)
        } catch (e: Exception) {
            trace.exception(status, e)
            throw e
        }

        return "ok"
    }
}