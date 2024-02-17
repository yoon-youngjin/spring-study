package hello.aop.proxyvs

import hello.aop.member.MemberService
import hello.aop.member.MemberServiceImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.aop.framework.ProxyFactory

class ProxyCastingTest {
    @Test
    fun jdkProxy() {
        val target = MemberServiceImpl()
        val proxyFactory = ProxyFactory(target)
        proxyFactory.isProxyTargetClass = false // JDK 동적 프록시

        // 프록시를 인터페이스로 캐스팅
        val memberServiceProxy = proxyFactory.proxy as MemberService

        // JDK 동적 프록시를 구현 클래스로 캐스팅 시도 실패, ClassCastException
        assertThatThrownBy {
            memberServiceProxy as MemberServiceImpl
        }.isInstanceOf(ClassCastException::class.java)
    }

    @Test
    fun cglibProxy() {
        val target = MemberServiceImpl()
        val proxyFactory = ProxyFactory(target)
        proxyFactory.isProxyTargetClass = true // CGLIB 프록시

        // 프록시를 인터페이스로 캐스팅
        val memberServiceProxy = proxyFactory.proxy as MemberService

        // JDK 동적 프록시를 구현 클래스로 캐스팅 시도 성공
        assertDoesNotThrow {
            memberServiceProxy as MemberServiceImpl
        }
    }
}