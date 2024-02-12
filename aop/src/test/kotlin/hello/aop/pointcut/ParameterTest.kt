package hello.aop.pointcut

import hello.aop.member.MemberService
import hello.aop.member.annotation.ClassAop
import hello.aop.member.annotation.MethodAop
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

private val logger = LoggerFactory.getLogger(ParameterTest::class.java)

@SpringBootTest
@Import(ParameterTest.ParameterAspect::class)
class ParameterTest(
    @Autowired
    private val memberService: MemberService,
) {

    @Test
    fun success() {
        logger.info("memberService Proxy=${memberService.javaClass}")
        memberService.hello("helloA")
    }

    @Aspect
    class ParameterAspect {

        @Pointcut("execution(* hello.aop.member..*.*(..))")
        fun allMember() {
        }

        @Around("allMember()")
        fun logArgs1(joinPoint: ProceedingJoinPoint): Any? {
            val arg1 = joinPoint.args[0]
            logger.info("[logArg1]${joinPoint.signature}, args=$arg1")
            return joinPoint.proceed()
        }

        @Around("allMember() && args(arg, ..)")
        fun logArgs2(joinPoint: ProceedingJoinPoint, arg: Any): Any? {
            logger.info("[logArg2]${joinPoint.signature}, args=$arg")
            return joinPoint.proceed()
        }

        @Before("allMember() && args(arg, ..)")
        fun logArgs3(arg: String) {
            logger.info("[logArg3]args=$arg")
        }

        @Before("allMember() && this(obj)")
        fun thisArgs(obj: MemberService) { // 실제 빈으로 등록된 프록시 객체가 들어옴
            logger.info("[this]obj=${obj.javaClass}")
        }

        @Before("allMember() && target(obj)")
        fun targetArgs(obj: MemberService) { // 실제 대상 구현체가 들어옴
            logger.info("[target]obj=${obj.javaClass}")
        }

        @Before("allMember() && @target(annotation)")
        fun atTarget(annotation: ClassAop) {
            logger.info("[@target]annotation=$annotation")
        }

        @Before("allMember() && @within(annotation)")
        fun atWithin(annotation: ClassAop) {
            logger.info("[@within]annotation=$annotation")
        }

        @Before("allMember() && @annotation(annotation)")
        fun atAnnotation(annotation: MethodAop) {
            logger.info("[@annotation]annotationValue=${annotation.value}")
        }
    }
}