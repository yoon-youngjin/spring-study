package hello.proxy.common.service

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(ConcreteService::class.java)

open class ConcreteService {
    open fun call() {
        logger.info("ConcreteService 호출")
    }
}
