package hello.advanced.app.v2

import hello.advanced.trace.TraceId
import hello.advanced.trace.TraceStatus
import hello.advanced.trace.hellotrace.HelloTraceV2
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class OrderServiceV2(
    private val orderRepositoryV2: OrderRepositoryV2,
    private val trace: HelloTraceV2,
) {
    fun orderItem(itemId: String, traceId: TraceId) {
        var status: TraceStatus? = null
        try {
            status = trace.beginSync(traceId, "OrderService.orderItem()")

            orderRepositoryV2.save(itemId, status.traceId)

            trace.end(status)
        } catch (e: Exception) {
            trace.exception(status, e)
            throw e
        }
    }
}