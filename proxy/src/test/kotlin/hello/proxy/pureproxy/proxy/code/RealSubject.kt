package hello.proxy.pureproxy.proxy.code

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(RealSubject::class.java.name)

class RealSubject : Subject {
    override fun operation(): String {
        logger.info("실제 객체 호출")
        Thread.sleep(1000)
        return "test"
    }
}
