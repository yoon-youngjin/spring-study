package hello.proxy.pureproxy.concreteproxy.code

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(TimeProxy::class.java.name)

class TimeProxy(
    private val realLogic: ConcreteLogic,
) : ConcreteLogic() {
    override fun operation(): String {
        logger.info("TimeProxy 실행")
        val startTime = System.currentTimeMillis()
        val result = realLogic.operation()
        val endTime = System.currentTimeMillis()
        logger.info("TimeProxy 종료 resultTime=${endTime - startTime}ms")
        return result
    }
}