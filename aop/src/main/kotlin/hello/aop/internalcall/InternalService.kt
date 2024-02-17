package hello.aop.internalcall

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

private val logger = LoggerFactory.getLogger(InternalService::class.java)

@Component
class InternalService {
    fun internal() {
        logger.info("call internal")
    }
}