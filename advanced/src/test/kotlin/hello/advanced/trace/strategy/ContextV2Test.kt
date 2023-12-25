package hello.advanced.trace.strategy

import hello.advanced.trace.strategy.code.strategy.ContextV2
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ContextV2Test")

internal class ContextV2Test {

    @Test
    fun strategyV1() {
        val context = ContextV2()
        context.execute {
            logger.info("비즈니르 로직1 실행")
        }
        context.execute {
            logger.info("비즈니르 로직2 실행")
        }
    }
}
