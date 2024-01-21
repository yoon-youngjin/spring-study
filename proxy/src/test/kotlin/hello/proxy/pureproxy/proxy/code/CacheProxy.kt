package hello.proxy.pureproxy.proxy.code

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(CacheProxy::class.java.name)

class CacheProxy(
    private val target: Subject,
) : Subject {
    private var cacheValue: String? = null
    override fun operation(): String {
        logger.info("프록시 호출")
        if (cacheValue == null) {
            cacheValue = target.operation()
        }
        return cacheValue!!
    }
}
