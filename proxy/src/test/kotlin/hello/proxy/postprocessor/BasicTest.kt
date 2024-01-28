package hello.proxy.postprocessor

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

internal class BasicTest {

    @Test
    fun basicConfig() {
        val applicationContext = AnnotationConfigApplicationContext(BasicConfig::class.java)

        // A는 빈으로 등록된다.
        val a = applicationContext.getBean("beanA", A::class.java)
        a.helloA()

        // B는 빈으로 등록되지 않는다.
        assertThrows<NoSuchBeanDefinitionException> {
            applicationContext.getBean("beanB")

        }
    }

    companion object {
        class A {
            private val logger = LoggerFactory.getLogger(A::class.java)
            fun helloA() {
                logger.info("hello A")
            }
        }

        class B {
            private val logger = LoggerFactory.getLogger(B::class.java)
            fun helloB() {
                logger.info("hello B")
            }
        }

        @Configuration
        class BasicConfig {

            @Bean(name = ["beanA"])
            fun a(): A {
                return A()
            }
        }
    }
}