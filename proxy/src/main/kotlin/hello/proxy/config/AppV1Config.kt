package hello.proxy.config

import hello.proxy.app.v1.OrderControllerV1
import hello.proxy.app.v1.OrderControllerV1Impl
import hello.proxy.app.v1.OrderRepositoryV1
import hello.proxy.app.v1.OrderRepositoryV1Impl
import hello.proxy.app.v1.OrderServiceV1
import hello.proxy.app.v1.OrderServiceV1Impl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppV1Config {
    @Bean
    fun orderControllerV1(): OrderControllerV1 {
        return OrderControllerV1Impl(orderServiceV1())
    }

    @Bean
    fun orderServiceV1(): OrderServiceV1 {
        return OrderServiceV1Impl(orderRepositoryV1())
    }

    @Bean
    fun orderRepositoryV1(): OrderRepositoryV1 {
        return OrderRepositoryV1Impl()
    }
}