package hello.proxy.config.v3_proxyfactory

import hello.proxy.app.v2.OrderControllerV2
import hello.proxy.app.v2.OrderRepositoryV2
import hello.proxy.app.v2.OrderServiceV2
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice
import hello.proxy.trace.logtrace.LogTrace
import org.springframework.aop.Advisor
import org.springframework.aop.framework.ProxyFactory
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.aop.support.NameMatchMethodPointcut
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ProxyFactoryConfigV2 {

    @Bean
    fun orderControllerV2(logTrace: LogTrace): OrderControllerV2 {
        val orderController = OrderControllerV2(orderServiceV2(logTrace))
        val proxyFactory = ProxyFactory(orderController)
        proxyFactory.addAdvisor(getAdvisor(logTrace))
        return proxyFactory.proxy as OrderControllerV2
    }


    @Bean
    fun orderServiceV2(logTrace: LogTrace): OrderServiceV2 {
        val orderService = OrderServiceV2(orderRepositoryV2(logTrace))
        val proxyFactory = ProxyFactory(orderService)
        proxyFactory.addAdvisor(getAdvisor(logTrace))
        return proxyFactory.proxy as OrderServiceV2
    }

    @Bean
    fun orderRepositoryV2(logTrace: LogTrace): OrderRepositoryV2 {
        val orderRepository = OrderRepositoryV2()
        val proxyFactory = ProxyFactory(orderRepository)
        proxyFactory.addAdvisor(getAdvisor(logTrace))
        return proxyFactory.proxy as OrderRepositoryV2
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
