package hello.aop.pointcut

import hello.aop.member.MemberServiceImpl
import java.lang.reflect.Method
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.aop.aspectj.AspectJExpressionPointcut

private val logger = LoggerFactory.getLogger(ExecutionTest::class.java)

class ExecutionTest {
    private val pointcut = AspectJExpressionPointcut()
    private lateinit var helloMethod: Method

    @BeforeEach
    fun init() {
        helloMethod = MemberServiceImpl::class.java.getMethod("hello", String::class.java)
    }

    @Test
    fun printMethod() {
        // public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        logger.info("helloMethod=$helloMethod")
    }

    @Test
    fun exactMatch() {
        // execution(접근제어자? 반환타입 선언타입?메서드이름(파라미터) 예외?)
        pointcut.expression = "execution(public String hello.aop.member.MemberServiceImpl.hello(String))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun allMatch() {
        pointcut.expression = "execution(* *(..))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun nameMatch() {
        pointcut.expression = "execution(* hello(..))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun nameMatchStar1() {
        pointcut.expression = "execution(* he*(..))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun nameMatchStar2() {
        pointcut.expression = "execution(* *ll*(..))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun nameMatchFalse() {
        pointcut.expression = "execution(* none(..))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isFalse()
    }

    @Test
    fun packageExactMatch1() {
        pointcut.expression = "execution(* hello.aop.member.MemberServiceImpl.hello(..))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun packageExactMatch2() {
        pointcut.expression = "execution(* hello.aop.member.*.*(..))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun packageMatchFalse() {
        pointcut.expression = "execution(* hello.aop.*.*(..))" // 현재 패키지가 hello.aop.member이기 때문에 실패
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isFalse()
    }

    @Test
    fun packageMatchSubPackage1() {
        pointcut.expression = "execution(* hello.aop.member..*.*(..))" // 하위 패키지 모두 포함
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun packageMatchSubPackage2() {
        pointcut.expression = "execution(* hello.aop..*.*(..))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun typeExactMatch() {
        pointcut.expression = "execution(* hello.aop.member.MemberServiceImpl.*(..))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun typeMatchSuperType() {
        pointcut.expression = "execution(* hello.aop.member.MemberService.*(..))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun typeMatchInternal() {
        pointcut.expression = "execution(* hello.aop.member.MemberServiceImpl.*(..))"
        val internalMethod = MemberServiceImpl::class.java.getMethod("internal", String::class.java)
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun typeMatchNoSuperTypeMethodFalse() {
        pointcut.expression = "execution(* hello.aop.member.MemberService.*(..))"
        val internalMethod = MemberServiceImpl::class.java.getMethod("internal", String::class.java)
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl::class.java)).isFalse()
    }

    @Test
    fun argsMatch() {
        pointcut.expression = "execution(* *(String))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun argsMatchNoArgs() {
        pointcut.expression = "execution(* *())"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isFalse()
    }

    // 정확히 하나의 파라미터 허용, 모든 타입 허용
    // -> 파라미터 두개 (*, *)
    @Test
    fun argsMatchStar() {
        pointcut.expression = "execution(* *(*))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    // 숫자와 무관하게 모든 파라미터, 모든 타입 허용
    @Test
    fun argsMatchAll() {
        pointcut.expression = "execution(* *(..))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    // String 타입으로 시작, 숫자와 무관하게 모든 파라미터, 모든 타입 허용
    @Test
    fun argsMatchComplex() {
        pointcut.expression = "execution(* *(String, ..))"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }
}