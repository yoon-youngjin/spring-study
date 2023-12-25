package hello.advanced.trace.strategy.code.template

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ContextV2")

class TimeLogTemplate {
    fun execute(callback: Callback) {
        val startTime = System.currentTimeMillis()
        // 비즈니스 로직 실행
        callback.call() // 위임
        // 비즈니스 로직 종료
        val endTime = System.currentTimeMillis()
        logger.info("resultTime=${endTime - startTime}")
    }
}