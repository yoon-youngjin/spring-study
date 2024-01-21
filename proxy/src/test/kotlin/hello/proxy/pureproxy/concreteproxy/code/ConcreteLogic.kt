package hello.proxy.pureproxy.concreteproxy.code

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(ConcreteLogic::class.java.name)

open class ConcreteLogic {
    open fun operation(): String {
        logger.info("ConcreteLogic 실행")
        return "data"
    }
}