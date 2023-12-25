package hello.advanced.trace.template.code

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("AbstractTemplate")
abstract class AbstractTemplate {
    fun execute() {
        val startTime = System.currentTimeMillis()
        // 비즈니스 로직 실행
        call() // 상속
        // 비즈니스 로직 종료
        val endTime = System.currentTimeMillis()
        logger.info("resultTime=${endTime - startTime}")
    }

    abstract fun call()
}