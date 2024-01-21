package hello.proxy.jdkdynamic

import java.lang.reflect.Method
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(ReflectionTest::class.java.name)

internal class ReflectionTest {

    @Test
    fun reflectionTest0() {
        val target = Hello()

        // 공통 로직1 시작
        logger.info("start")
        val result1 = target.callA()
        logger.info("result=$result1")
        // 공통 로직1 종료

        // 공통 로직2 시작
        logger.info("start")
        val result2 = target.callB()
        logger.info("result=$result2")
        // 공통 로직2 종료
    }

    @Test
    fun reflectionTest1() {
        // 클래스 정보
//        val classHello = Class.forName("hello.proxy.jdkdynamic.Hello")
        val classHello = Hello::class.java

        val target = Hello()

        // callA 메서드 정보
        val methodCallA = classHello.getMethod("callA")
        val result1 = methodCallA(target)
        logger.info("result1=$result1")

        // callB 메서드 정보
        val methodCallB = classHello.getMethod("callB")
        val result2 = methodCallB(target)
        logger.info("result2=$result2")
    }

    @Test
    fun reflectionTest2() {
        // 클래스 정보
        val classHello = Hello::class.java

        val target = Hello()

        val methodCallA = classHello.getMethod("callA")
        dynamicCall(methodCallA, target)

        val methodCallB = classHello.getMethod("callB")
        dynamicCall(methodCallB, target)
    }

    private fun dynamicCall(method: Method, target: Any) {
        // callA 메서드 정보
        logger.info("start")
        val result = method.invoke(target)
        logger.info("result=$result")
    }
}

class Hello {
    fun callA(): String {
        logger.info("callA")
        return "A"
    }

    fun callB(): String {
        logger.info("callB")
        return "B"
    }
}