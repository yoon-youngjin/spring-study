package hello.proxy.app.v1

class OrderControllerV1Impl(
    private val orderService: OrderServiceV1,
) : OrderControllerV1 {
    override fun request(itemId: String): String {
        orderService.orderItem(itemId)
        return "ok"
    }

    override fun noLog(): String {
        return "ok"
    }
}