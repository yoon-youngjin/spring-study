package hello.aop.internalcall

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

private val logger = LoggerFactory.getLogger(CallServiceV1::class.java)

@Component
class CallServiceV1 {

    @Autowired
    @Lazy
    private lateinit var callServiceV1: CallServiceV1

    fun external() {
        logger.info("call external")
        callServiceV1.internal()
    }

    fun internal() {
        logger.info("call internal")
    }
}