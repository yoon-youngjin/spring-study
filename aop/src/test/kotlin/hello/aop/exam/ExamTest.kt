package hello.aop.exam

import hello.aop.exam.aop.RetryAspect
import hello.aop.exam.aop.TraceAspect
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

private val logger = LoggerFactory.getLogger(ExamTest::class.java)

@Import(TraceAspect::class, RetryAspect::class)
@SpringBootTest
class ExamTest(
    @Autowired
    private val examService: ExamService,
) {
    @Test
    fun test() {
        for (i in 0..4) {
            logger.info("client request i=$i")
            examService.request("data$i")
        }
    }
}