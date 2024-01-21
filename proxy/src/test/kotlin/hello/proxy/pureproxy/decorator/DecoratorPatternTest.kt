package hello.proxy.pureproxy.decorator

import hello.proxy.pureproxy.decorator.code.DecoratorPatternClient
import hello.proxy.pureproxy.decorator.code.MessageDecorator
import hello.proxy.pureproxy.decorator.code.RealComponent
import hello.proxy.pureproxy.decorator.code.TimeDecorator
import org.junit.jupiter.api.Test

internal class DecoratorPatternTest {

    @Test
    fun noDecoratorTest() {
        val realComponent = RealComponent()
        val client = DecoratorPatternClient(realComponent)
        client.execute()
    }

    @Test
    fun decoratorTest1() {
        val messageDecorator = MessageDecorator(RealComponent())
        val client = DecoratorPatternClient(messageDecorator)
        client.execute()
    }

    @Test
    fun decoratorTest2() {
        val messageDecorator = MessageDecorator(RealComponent())
        val timeDecorator = TimeDecorator(messageDecorator)
        val client = DecoratorPatternClient(timeDecorator)
        client.execute()
    }
}
