package hello.advanced.trace.template

import hello.advanced.trace.template.code.AbstractTemplate
import hello.advanced.trace.template.code.TemplatePattern
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("TemplateMethodTest")

internal class TemplateMethodTest {

    @Test
    fun templateMethodV0() {
//        logic1()
//        logic2()
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

    @Test
    fun templateMethodV1() {
        TemplatePattern().execute {
            logger.info("비즈니스 로직1 실행")
        }

        TemplatePattern().execute {
            logger.info("비즈니스 로직1 실행")
        }
    }

    @Test
    fun templateMethodV2() {
        val template1 = object : AbstractTemplate() {
            override fun call() {
                logger.info("비즈니스 로직1 실행")
            }
        }

        val template2 = object : AbstractTemplate() {
            override fun call() {
                logger.info("비즈니스 로직2 실행")
            }
        }
        template1.execute()
        template2.execute()
    }
}