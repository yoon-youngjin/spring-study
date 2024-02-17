package hello.aop.internalcall

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

private val logger = LoggerFactory.getLogger(CallServiceV0::class.java)
@Component
class CallServiceV0 {

    fun external() {
        logger.info("call external")
        internal()
    }

    fun internal() {
        logger.info("call internal")
    }
}