package hello.proxy.app.v2

open class OrderRepositoryV2 {
    open fun save(itemId: String) {
        if (itemId == "ex") {
            throw IllegalStateException("예외 발생!")
        }
        Thread.sleep(1000)
    }
}