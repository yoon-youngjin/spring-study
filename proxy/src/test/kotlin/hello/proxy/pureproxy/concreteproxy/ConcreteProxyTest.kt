package hello.proxy.pureproxy.concreteproxy

import hello.proxy.pureproxy.concreteproxy.code.ConcreteClient
import hello.proxy.pureproxy.concreteproxy.code.ConcreteLogic
import hello.proxy.pureproxy.concreteproxy.code.TimeProxy
import org.junit.jupiter.api.Test

class ConcreteProxyTest {

    @Test
    fun noProxy() {
        val concreteLogic = ConcreteLogic()
        val client = ConcreteClient(concreteLogic)
        client.execute()
    }

    @Test
    fun addProxy() {
        val concreteLogic = ConcreteLogic()
        val timeProxy = TimeProxy(concreteLogic)
        val client = ConcreteClient(timeProxy)
        client.execute()
    }

}