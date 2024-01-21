package hello.proxy.pureproxy.decorator.code

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(RealComponent::class.java.name)

class MessageDecorator(
    private val component: Component,
) : Component {
    override fun operation(): String {
        logger.info("MessageDecorator 실행");
        val result = component.operation()
        val decoResult = "*****$result*****"

        logger.info(
            "MessageDecorator 꾸미기 적용 전=$result, 적용 후=$decoResult"
        )
        return decoResult
    }
}
