package hello.advanced.trace.strategy.code.strategy

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("StrategyLogic2")
class StrategyLogic2: Strategy {
    override fun call() {
        logger.info("비즈니스 로직2 실행")
    }
}

