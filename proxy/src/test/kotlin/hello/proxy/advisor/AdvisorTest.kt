package hello.proxy.advisor

import hello.proxy.common.advice.TimeAdvice
import hello.proxy.common.service.ServiceImpl
import hello.proxy.common.service.ServiceInterface
import java.lang.reflect.Method
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.aop.ClassFilter
import org.springframework.aop.MethodMatcher
import org.springframework.aop.Pointcut
import org.springframework.aop.framework.ProxyFactory
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.aop.support.NameMatchMethodPointcut

private val logger = LoggerFactory.getLogger(AdvisorTest::class.java)

internal class AdvisorTest {

    @Test
    fun advisorTest1() {
        val target = ServiceImpl()
        val proxyFactory = ProxyFactory(target)
        val advisor = DefaultPointcutAdvisor(Pointcut.TRUE, TimeAdvice())
        proxyFactory.addAdvisor(advisor)
        val proxy = proxyFactory.proxy as ServiceInterface

        proxy.find()
    }

    @Test
    @DisplayName("직접 만든 포인트컷")
    fun advisorTest2() {
        val target = ServiceImpl()
        val proxyFactory = ProxyFactory(target)
        val advisor = DefaultPointcutAdvisor(MyPointCut(), TimeAdvice())
        proxyFactory.addAdvisor(advisor)
        val proxy = proxyFactory.proxy as ServiceInterface

        proxy.save()
        proxy.find()
    }

    @Test
    @DisplayName("스프링이 제공하는 포인트컷")
    fun advisorTest3() {
        val target = ServiceImpl()
        val proxyFactory = ProxyFactory(target)
        val pointCut = NameMatchMethodPointcut()
        pointCut.setMappedNames("save")
        val advisor = DefaultPointcutAdvisor(pointCut, TimeAdvice())
        proxyFactory.addAdvisor(advisor)
        val proxy = proxyFactory.proxy as ServiceInterface

        proxy.save()
        proxy.find()
    }

    companion object {
        class MyPointCut : Pointcut {
            override fun getClassFilter(): ClassFilter {
                return ClassFilter.TRUE
            }

            override fun getMethodMatcher(): MethodMatcher {
                return MyMethodMatcher()
            }

        }

        class MyMethodMatcher : MethodMatcher {
            private val matchName = "save"

            override fun matches(method: Method, targetClass: Class<*>): Boolean {
                val result = method.name.equals(matchName)
                logger.info("PointCut 호출 method=${method.name} targetClass=${targetClass}")
                logger.info("PointCut 결과 result=$result")
                return result
            }

            override fun matches(method: Method, targetClass: Class<*>, vararg args: Any?): Boolean {
                return false
            }

            override fun isRuntime(): Boolean {
                return false
            }

        }
    }
}
