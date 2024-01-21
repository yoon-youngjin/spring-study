package hello.proxy.pureproxy.decorator.code

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(RealComponent::class.java.name)

class RealComponent: Component {
    override fun operation(): String {
        logger.info("RealComponent 실행")
        return "data"
    }
}
