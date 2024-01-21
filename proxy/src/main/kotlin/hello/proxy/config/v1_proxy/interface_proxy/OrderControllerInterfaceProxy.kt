package hello.proxy.config.v1_proxy.interface_proxy

import hello.advanced.trace.TraceStatus
import hello.proxy.app.v1.OrderControllerV1
import hello.proxy.trace.logtrace.LogTrace

class OrderControllerInterfaceProxy(
    private val target: OrderControllerV1,
    private val logTrace: LogTrace,
) : OrderControllerV1 {
    override fun request(itemId: String): String {
        var status: TraceStatus? = null
        return try {
            status = logTrace.begin("OrderController.request()")
            // target 호출
            val result = target.request(itemId)
            logTrace.end(status)
            result
        } catch (e: Exception) {
            logTrace.exception(status, e)
            throw e
        }
    }

    override fun noLog(): String {
        return target.noLog()
    }
}