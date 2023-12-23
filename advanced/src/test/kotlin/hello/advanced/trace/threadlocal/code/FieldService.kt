package hello.advanced.trace.threadlocal.code

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("FieldService")

class FieldService {

    private var nameStore: String? = null

    fun logic(name: String) {
        logger.info("저장 name=$name -> nameStore=$nameStore")
        nameStore = name
        Thread.sleep(1000)
        logger.info("조회 nameStore=$nameStore")
    }
}