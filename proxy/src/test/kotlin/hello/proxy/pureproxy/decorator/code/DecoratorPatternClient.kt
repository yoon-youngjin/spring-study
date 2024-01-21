package hello.proxy.pureproxy.decorator.code

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(DecoratorPatternClient::class.java.name)

class DecoratorPatternClient(
    private val component: Component,
) {
    fun execute() {
        val result = component.operation()
        logger.info("result=${result}")
    }
}
