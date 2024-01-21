package hello.proxy.proxyfactory

import hello.proxy.common.advice.TimeAdvice
import hello.proxy.common.service.ConcreteService
import hello.proxy.common.service.ServiceImpl
import hello.proxy.common.service.ServiceInterface
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.aop.framework.ProxyFactory
import org.springframework.aop.support.AopUtils

private val logger = LoggerFactory.getLogger(ProxyFactoryTest::class.java)
internal class ProxyFactoryTest {
    @Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
    fun interfaceProxy() {
        val target = ServiceImpl()
        val proxyFactory = ProxyFactory(target)
        proxyFactory.addAdvice(TimeAdvice())
        val proxy = proxyFactory.proxy as ServiceInterface
        logger.info("targetClass=${target.javaClass}")
        logger.info("proxyClass=${proxy.javaClass}")

        proxy.save()

        Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue()
        Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue()
        Assertions.assertThat(AopUtils.isCglibProxy(proxy)).isFalse()
    }

    @Test
    @DisplayName("구체 클래스만 있으면 CGLIB 사용")
    fun concreteProxy() {
        val target = ConcreteService()
        val proxyFactory = ProxyFactory(target)
        proxyFactory.addAdvice(TimeAdvice())
        val proxy = proxyFactory.proxy as ConcreteService
        logger.info("targetClass=${target.javaClass}")
        logger.info("proxyClass=${proxy.javaClass}")

        proxy.call()

        Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue()
        Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse()
        Assertions.assertThat(AopUtils.isCglibProxy(proxy)).isTrue()
    }

    @Test
    @DisplayName("ProxyTargetClass 옵션을 사용하면 인터페이스가 있어도 CGLIB를 사용하고, 클래스 기반 프록시 사용한다")
    fun proxyTargetTest() {
        val target = ServiceImpl()
        val proxyFactory = ProxyFactory(target)
        proxyFactory.addAdvice(TimeAdvice())
        proxyFactory.isProxyTargetClass = true
        val proxy = proxyFactory.proxy as ServiceInterface
        logger.info("targetClass=${target.javaClass}")
        logger.info("proxyClass=${proxy.javaClass}")

        proxy.save()

        Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue()
        Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse()
        Assertions.assertThat(AopUtils.isCglibProxy(proxy)).isTrue()
    }
}