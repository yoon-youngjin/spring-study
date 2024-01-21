package hello.proxy.jdkdynamic.code

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(AImpl::class.java.name)

class AImpl: AInterface {
    override fun call(): String {
        logger.info("A 호출")
        return "a"
    }
}