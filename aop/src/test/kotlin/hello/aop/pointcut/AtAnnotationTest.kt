package hello.aop.pointcut

import hello.aop.member.MemberService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

private val logger = LoggerFactory.getLogger(AtAnnotationTest::class.java)

@Import(AtAnnotationTest.AtAnnotationAspect::class)
@SpringBootTest
class AtAnnotationTest(
    @Autowired
    private val memberService: MemberService,
) {
    @Test
    fun success() {
        logger.info("memberService Proxy=${memberService.javaClass}")
        memberService.hello("helloA")
    }

    @Aspect
    class AtAnnotationAspect {

        @Around("@annotation(hello.aop.member.annotation.MethodAop)")
        fun doAtAnnotation(joinPoint: ProceedingJoinPoint): Any? {
            logger.info("[@annotation] ${joinPoint.signature}")
            return joinPoint.proceed()
        }
    }
}