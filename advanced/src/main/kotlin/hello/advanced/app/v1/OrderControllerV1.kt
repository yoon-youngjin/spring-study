package hello.advanced.app.v1

import hello.advanced.trace.TraceStatus
import hello.advanced.trace.hellotrace.HelloTraceV1
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class OrderControllerV1(
    private val orderServiceV1: OrderServiceV1,
    private val trace: HelloTraceV1,
) {

    @GetMapping("/v1/request")
    fun request(itemId: String): String {

        var status: TraceStatus? = null
        try {
            status = trace.begin("OrderController.request()")
            orderServiceV1.orderItem(itemId)
            trace.end(status)
        } catch (e: Exception) {
            trace.exception(status, e)
            throw e
        }

        return "ok"
    }
}