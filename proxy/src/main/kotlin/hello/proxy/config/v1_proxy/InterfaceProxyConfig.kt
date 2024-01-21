package hello.proxy.config.v1_proxy

import hello.proxy.app.v1.OrderControllerV1
import hello.proxy.app.v1.OrderControllerV1Impl
import hello.proxy.app.v1.OrderRepositoryV1
import hello.proxy.app.v1.OrderRepositoryV1Impl
import hello.proxy.app.v1.OrderServiceV1
import hello.proxy.app.v1.OrderServiceV1Impl
import hello.proxy.config.v1_proxy.interface_proxy.OrderControllerInterfaceProxy
import hello.proxy.config.v1_proxy.interface_proxy.OrderRepositoryInterfaceProxy
import hello.proxy.config.v1_proxy.interface_proxy.OrderServiceInterfaceProxy
import hello.proxy.trace.logtrace.LogTrace
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InterfaceProxyConfig {
    @Bean
    fun orderController(logTrace: LogTrace): OrderControllerV1 {
        return OrderControllerInterfaceProxy(
            target = OrderControllerV1Impl(orderService(logTrace)),
            logTrace = logTrace,
        )
    }

    @Bean
    fun orderService(logTrace: LogTrace): OrderServiceV1 {
        return OrderServiceInterfaceProxy(
            target = OrderServiceV1Impl(orderRepository(logTrace)),
            logTrace = logTrace,
        )
    }

    @Bean
    fun orderRepository(logTrace: LogTrace): OrderRepositoryV1 {
        return OrderRepositoryInterfaceProxy(
            target = OrderRepositoryV1Impl(),
            logTrace = logTrace,
        )
    }
}