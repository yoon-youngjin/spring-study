package hello.proxy.jdkdynamic

import hello.proxy.jdkdynamic.code.AImpl
import hello.proxy.jdkdynamic.code.AInterface
import hello.proxy.jdkdynamic.code.BImpl
import hello.proxy.jdkdynamic.code.BInterface
import hello.proxy.jdkdynamic.code.TimeInvocationHandler
import java.lang.reflect.Proxy
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory


private val logger = LoggerFactory.getLogger(JdkDynamicProxyTest::class.java.name)

internal class JdkDynamicProxyTest {

    @Test
    fun dynamicA() {
        val target = AImpl()
        val handler = TimeInvocationHandler(target)

        val proxy = Proxy.newProxyInstance(
            AInterface::class.java.getClassLoader(),
            arrayOf(AInterface::class.java),
            handler,
        ) as AInterface

        proxy.call() // call() 메서드를 TimeInvocationHandler invoke 메서드의 method 파라미터로 넘겨준다
        logger.info("targetClass=${target::class.java}")
        logger.info("proxyClass=${proxy::class.java}")
    }

    @Test
    fun dynamicB() {
        val target = BImpl()
        val handler = TimeInvocationHandler(target)

        val proxy = Proxy.newProxyInstance(
            BInterface::class.java.getClassLoader(),
            arrayOf(BInterface::class.java),
            handler,
        ) as BInterface

        proxy.call()
        logger.info("targetClass=${target::class.java}")
        logger.info("proxyClass=${proxy::class.java}")
    }
}