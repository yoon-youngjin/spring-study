package hello.aop.internalcall

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

private val logger = LoggerFactory.getLogger(CallServiceV3::class.java)

@Component
class CallServiceV3(
    private val internalService: InternalService,
) {
    fun external() {
        logger.info("call external")
        internalService.internal()
    }
}