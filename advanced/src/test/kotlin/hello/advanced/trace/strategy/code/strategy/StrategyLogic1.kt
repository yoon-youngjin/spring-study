package hello.advanced.trace.strategy.code.strategy

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("StrategyLogic1")

class StrategyLogic1: Strategy {
    override fun call() {
        logger.info("비즈니스 로직1 실행")
    }
}