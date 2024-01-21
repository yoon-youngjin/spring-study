package hello.proxy.app.v2

open class OrderServiceV2(
    private val orderRepository: OrderRepositoryV2?,
) {
    open fun orderItem(itemId: String) {
        orderRepository!!.save(itemId)
    }
}