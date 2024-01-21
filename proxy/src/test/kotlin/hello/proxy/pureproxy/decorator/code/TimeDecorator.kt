package hello.proxy.pureproxy.decorator.code

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(RealComponent::class.java.name)

class TimeDecorator(
    private val component: Component,
) : Component {
    override fun operation(): String {
        logger.info("TimeDecorator 실행")
        val startTime = System.currentTimeMillis()
        val result = component.operation()
        val endTime = System.currentTimeMillis()
        logger.info("TimeDecorator 종료 resultTime=${endTime - startTime}ms")
        return result
    }
}
