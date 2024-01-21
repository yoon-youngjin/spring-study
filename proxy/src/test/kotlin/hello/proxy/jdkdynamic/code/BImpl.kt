package hello.proxy.jdkdynamic.code

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(BImpl::class.java.name)

class BImpl : BInterface {
    override fun call(): String {
        logger.info("B 호출")
        return "b"
    }
}