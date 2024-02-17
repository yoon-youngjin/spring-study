package hello.aop.internalcall

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component

private val logger = LoggerFactory.getLogger(CallServiceV1::class.java)

@Component
class CallServiceV2(
    private val callServiceProvider: ObjectProvider<CallServiceV2>,
) {
    fun external() {
        logger.info("call external")
        val callServiceV2 = callServiceProvider.getObject()
        callServiceV2.internal()
    }

    fun internal() {
        logger.info("call internal")
    }
}