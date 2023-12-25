package hello.advanced.trace.strategy

import hello.advanced.trace.strategy.code.strategy.ContextV1
import hello.advanced.trace.strategy.code.strategy.StrategyLogic1
import hello.advanced.trace.strategy.code.strategy.StrategyLogic2
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ContextV1Test")

internal class ContextV1Test {

    @Test
    fun templateMethodV0() {
        logic1()
        logic2()
    }

    private fun logic1() {
        val startTime = System.currentTimeMillis()
        // 비즈니스 로직 실행
        logger.info("비즈니스 로직1 실행")
        // 비즈니스 로직 종료
        val endTime = System.currentTimeMillis()
        logger.info("resultTime=${endTime - startTime}")
    }

    private fun logic2() {
        val startTime = System.currentTimeMillis()
        // 비즈니스 로직 실행
        logger.info("비즈니스 로직2 실행")
        // 비즈니스 로직 종료
        val endTime = System.currentTimeMillis()
        logger.info("resultTime=${endTime - startTime}")
    }

    /**
     * 전략 패턴 사용
     */
    @Test
    fun strategyV1() {
        val strategy1 = StrategyLogic1()
        val context1 = ContextV1(strategy1)
        context1.execute()

        val strategy2 = StrategyLogic2()
        val context2 = ContextV1(strategy2)
        context2.execute()
    }

    @Test
    fun strategyV2() {
        val context1 = ContextV1 {
            logger.info("비즈니스 로직1 실행")
        }
        context1.execute()

        val context2 = ContextV1 {
            logger.info("비즈니스 로직2 실행")
        }
        context2.execute()
    }
}