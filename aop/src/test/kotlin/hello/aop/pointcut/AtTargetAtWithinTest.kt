package hello.aop.pointcut

import hello.aop.member.annotation.ClassAop
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import


private val logger = LoggerFactory.getLogger(AtTargetAtWithinTest::class.java)

@Import(AtTargetAtWithinTest.Config::class)
@SpringBootTest
class AtTargetAtWithinTest(
    @Autowired
    private val child: Child,
) {
    open class Parent {
        fun parentMethod() {}
    }

    @ClassAop
    class Child : Parent() {
        fun childMethod() {}
    }

    @Test
    fun success() {
        logger.info("child Proxy={}", child.javaClass)
        child.childMethod() //부모, 자식 모두 있는 메서드
        child.parentMethod() //부모 클래스만 있는 메서드
    }

    class Config {
        @Bean
        fun parent(): Parent {
            return Parent()
        }
        @Bean
        fun child(): Child {
            return Child()
        }
        @Bean
        fun atTargetAtWithinAspect(): AtTargetAtWithinAspect {
            return AtTargetAtWithinAspect()
        }
    }

    @Aspect
    class AtTargetAtWithinAspect {
        //@target: 인스턴스 기준으로 모든 메서드의 조인 포인트를 선정, 부모 타입의 메서드도 적용
        @Around("execution(* hello.aop..*(..)) && @target(hello.aop.member.annotation.ClassAop)")
        fun atTarget(joinPoint: ProceedingJoinPoint): Any? {
            logger.info("[@target] {}", joinPoint.signature);
            return joinPoint.proceed()
        }

        @Around("execution(* hello.aop..*(..)) && @within(hello.aop.member.annotation.ClassAop)")
        fun atWithin(joinPoint: ProceedingJoinPoint): Any? {
            logger.info("[@within] {}", joinPoint.signature);
            return joinPoint.proceed()
        }
    }
}