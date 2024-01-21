package hello.proxy.config.v2_dynamicproxy

import hello.proxy.app.v1.OrderControllerV1
import hello.proxy.app.v1.OrderControllerV1Impl
import hello.proxy.app.v1.OrderRepositoryV1
import hello.proxy.app.v1.OrderRepositoryV1Impl
import hello.proxy.app.v1.OrderServiceV1
import hello.proxy.app.v1.OrderServiceV1Impl
import hello.proxy.config.v2_dynamicproxy.handler.LogTraceBasicHandler
import hello.proxy.trace.logtrace.LogTrace
import java.lang.reflect.Proxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DynamicProxyBasicConfig {
    @Bean
    fun orderControllerV1(logTrace: LogTrace): OrderControllerV1 {
        val orderController = OrderControllerV1Impl(orderServiceV1(logTrace))
        return Proxy.newProxyInstance(
            OrderControllerV1::class.java.getClassLoader(),
            arrayOf(OrderControllerV1::class.java),
            LogTraceBasicHandler(orderController, logTrace)
        ) as OrderControllerV1
    }

    @Bean
    fun orderServiceV1(logTrace: LogTrace): OrderServiceV1 {
        val orderService = OrderServiceV1Impl(orderRepositoryV1(logTrace))
        return Proxy.newProxyInstance(
            OrderServiceV1::class.java.getClassLoader(),
            arrayOf(OrderServiceV1::class.java),
            LogTraceBasicHandler(orderService, logTrace)
        ) as OrderServiceV1
    }

    @Bean
    fun orderRepositoryV1(logTrace: LogTrace): OrderRepositoryV1 {
        val orderRepository = OrderRepositoryV1Impl()
        return Proxy.newProxyInstance(
            OrderRepositoryV1::class.java.getClassLoader(),
            arrayOf(OrderRepositoryV1::class.java),
            LogTraceBasicHandler(orderRepository, logTrace)
        ) as OrderRepositoryV1
    }
}
