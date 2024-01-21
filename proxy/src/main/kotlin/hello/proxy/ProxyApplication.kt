package hello.proxy

import hello.proxy.config.BaseConfig
import hello.proxy.config.v3_proxyfactory.ProxyFactoryConfigV1
import hello.proxy.config.v3_proxyfactory.ProxyFactoryConfigV2
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

//@Import(AppV1Config::class, AppV2Config::class)
//@Import(InterfaceProxyConfig::class, BaseConfig::class)
//@Import(ConcreteProxyConfig::class, BaseConfig::class)
//@Import(DynamicProxyBasicConfig::class, BaseConfig::class)
//@Import(DynamicProxyFilterConfig::class, BaseConfig::class)
//@Import(ProxyFactoryConfigV1::class, BaseConfig::class)
@Import(ProxyFactoryConfigV2::class, BaseConfig::class)
@SpringBootApplication(scanBasePackages = ["hello.proxy.app.v2"])
class ProxyApplication

fun main(args: Array<String>) {
    runApplication<ProxyApplication>(*args)
}
