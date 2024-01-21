package hello.proxy.common.service

import org.slf4j.LoggerFactory
import kotlin.math.log

private val logger = LoggerFactory.getLogger(ServiceImpl::class.java)
open class ServiceImpl: ServiceInterface {
    override fun save() {
        logger.info("save 호출")
    }

    override fun find() {
        logger.info("find 호출")
    }
}