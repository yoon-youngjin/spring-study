package hello.advanced.trace.strategy

import hello.advanced.trace.strategy.code.template.TimeLogTemplate
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("TemplateCallbackTest")

class TemplateCallbackTest {

    @Test
    fun strategyV1() {
        val template = TimeLogTemplate()
        template.execute { logger.info("비즈니르 로직1 실행") }
        template.execute { logger.info("비즈니르 로직2 실행") }
    }
}