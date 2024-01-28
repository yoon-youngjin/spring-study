package hello.proxy.postprocessor

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val logger = LoggerFactory.getLogger(BeanPostProcessorTest.Companion.A::class.java)

internal class BeanPostProcessorTest {

    @Test
    fun basicConfig() {
        val applicationContext = AnnotationConfigApplicationContext(BeanPostProcessorConfig::class.java)

        // A는 빈으로 등록된다.
        val a = applicationContext.getBean("beanA", B::class.java)
        a.helloB()

        // B는 빈으로 등록되지 않는다.
        assertThrows<NoSuchBeanDefinitionException> {
            applicationContext.getBean("beanB")

        }
    }

    companion object {
        class A {
            fun helloA() {
                logger.info("hello A")
            }
        }

        class B {
            fun helloB() {
                logger.info("hello B")
            }
        }

        @Configuration
        class BeanPostProcessorConfig {
            @Bean(name = ["beanA"])
            fun a(): A {
                return A()
            }
            @Bean
            fun helloPostProcessor(): AToBPostProcessor {
                return AToBPostProcessor()
            }
        }

        class AToBPostProcessor : BeanPostProcessor {
            override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
                logger.info("beanName=$beanName, bean=$bean")
                return if (bean is A) {
                    B()
                }
                else {
                    bean
                }
            }

        }
    }
}