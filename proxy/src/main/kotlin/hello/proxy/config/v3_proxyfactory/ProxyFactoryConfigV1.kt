package hello.proxy.config.v3_proxyfactory

import hello.proxy.app.v1.OrderControllerV1
import hello.proxy.app.v1.OrderControllerV1Impl
import hello.proxy.app.v1.OrderRepositoryV1
import hello.proxy.app.v1.OrderRepositoryV1Impl
import hello.proxy.app.v1.OrderServiceV1
import hello.proxy.app.v1.OrderServiceV1Impl
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice
import hello.proxy.trace.logtrace.LogTrace
import org.springframework.aop.Advisor
import org.springframework.aop.framework.ProxyFactory
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.aop.support.NameMatchMethodPointcut
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ProxyFactoryConfigV1 {

    @Bean
    fun orderControllerV1(logTrace: LogTrace): OrderControllerV1 {
        val orderController = OrderControllerV1Impl(orderServiceV1(logTrace))
        val proxyFactory = ProxyFactory(orderController)
        proxyFactory.addAdvisor(getAdvisor(logTrace))
        return proxyFactory.proxy as OrderControllerV1
    }


    @Bean
    fun orderServiceV1(logTrace: LogTrace): OrderServiceV1 {
        val orderService = OrderServiceV1Impl(orderRepositoryV1(logTrace))
        val proxyFactory = ProxyFactory(orderService)
        proxyFactory.addAdvisor(getAdvisor(logTrace))
        return proxyFactory.proxy as OrderServiceV1
    }

    @Bean
    fun orderRepositoryV1(logTrace: LogTrace): OrderRepositoryV1 {
        val orderRepository = OrderRepositoryV1Impl()
        val proxyFactory = ProxyFactory(orderRepository)
        proxyFactory.addAdvisor(getAdvisor(logTrace))
        return proxyFactory.proxy as OrderRepositoryV1
    }

    private fun getAdvisor(logTrace: LogTrace): Advisor {
        //pointcut
        val pointcut = NameMatchMethodPointcut()
        pointcut.setMappedNames("request*", "order*", "save*")
        //advice
        val advice = LogTraceAdvice(logTrace)
        //advisor = pointcut + advice
        return DefaultPointcutAdvisor(pointcut, advice)
    }

}
