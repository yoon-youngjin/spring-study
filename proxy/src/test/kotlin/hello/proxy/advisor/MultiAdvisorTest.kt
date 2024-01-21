package hello.proxy.advisor

import hello.proxy.common.advice.TimeAdvice
import hello.proxy.common.service.ServiceImpl
import hello.proxy.common.service.ServiceInterface
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.aop.Pointcut
import org.springframework.aop.framework.ProxyFactory
import org.springframework.aop.support.DefaultPointcutAdvisor

private val logger = LoggerFactory.getLogger(MultiAdvisorTest::class.java)

internal class MultiAdvisorTest {

    @Test
    @DisplayName("여러 프록시")
    fun multiAdvisorTest1() {
        // client -> proxy2(advisor2) -> proxy1(advisor1) -> target

        // 프록시1 생성
        val target = ServiceImpl()
        val proxyFactory1 = ProxyFactory(target)
        val advisor1 = DefaultPointcutAdvisor(Pointcut.TRUE, Advice1())
        proxyFactory1.addAdvisor(advisor1)
        val proxy1 = proxyFactory1.proxy as ServiceInterface

        // 프록시2 생성
        val proxyFactory2 = ProxyFactory(proxy1)
        val advisor2 = DefaultPointcutAdvisor(Pointcut.TRUE, Advice2())
        proxyFactory2.addAdvisor(advisor2)
        val proxy2 = proxyFactory2.proxy as ServiceInterface

        proxy2.save()
    }

    @Test
    @DisplayName("하나의 프록시, 여러 어드바이저")
    fun multiAdvisorTest2() {
        val advisor2 = DefaultPointcutAdvisor(Pointcut.TRUE, Advice2())
        val advisor1 = DefaultPointcutAdvisor(Pointcut.TRUE, Advice1())

        val target = ServiceImpl()
        val proxyFactory = ProxyFactory(target)
        proxyFactory.addAdvisor(advisor2)
        proxyFactory.addAdvisor(advisor1)
        val proxy = proxyFactory.proxy as ServiceInterface

        proxy.save()
    }
    companion object {
        class Advice1 : MethodInterceptor {
            override fun invoke(invocation: MethodInvocation): Any? {
                logger.info("advice1 호출")
                return invocation.proceed()
            }
        }

        class Advice2 : MethodInterceptor {
            override fun invoke(invocation: MethodInvocation): Any? {
                logger.info("advice2 호출")
                return invocation.proceed()
            }
        }
    }
}