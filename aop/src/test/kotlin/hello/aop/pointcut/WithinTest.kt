package hello.aop.pointcut

import hello.aop.member.MemberServiceImpl
import java.lang.reflect.Method
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.aop.aspectj.AspectJExpressionPointcut

class WithinTest {
    private val pointcut = AspectJExpressionPointcut()
    private lateinit var helloMethod: Method

    @BeforeEach
    fun init() {
        helloMethod = MemberServiceImpl::class.java.getMethod("hello", String::class.java)
    }

    @Test
    fun withinExact() {
        pointcut.expression = "within(hello.aop.member.MemberServiceImpl)"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun withinStar() {
        pointcut.expression = "within(hello.aop.member.*Service*)"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    fun withinStubPackage() {
        pointcut.expression = "within(hello.aop..*)"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isTrue()
    }

    @Test
    @DisplayName("타겟의 타입에만 직접 적용, 인터페이스를 선정하면 안된다.")
    fun withinSuperTypeFalse() {
        pointcut.expression = "within(hello.aop.member.MemberService)"
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl::class.java)).isFalse()
    }
}