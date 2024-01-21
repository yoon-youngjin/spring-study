package hello.proxy.cglib

import hello.proxy.cglib.code.TimeMethodInterceptor
import hello.proxy.common.service.ConcreteService
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.cglib.proxy.Enhancer

private val logger = LoggerFactory.getLogger(CglibTest::class.java)
class CglibTest {

    @Test
    fun cglib() {
        val target = ConcreteService()

        val enhancer = Enhancer().apply {
//            setSuperclass(ConcreteService::class.java)
            setCallback(TimeMethodInterceptor(target))
        }
        val proxy = enhancer.create() as ConcreteService
        logger.info("targetClass=${target.javaClass}")
        logger.info("proxyClass=${proxy.javaClass}")

        proxy.call()
    }
}
