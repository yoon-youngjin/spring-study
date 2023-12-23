package hello.advanced.trace.threadlocal.code

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("FieldService")

class ThreadLocalService {

    private var nameStore: ThreadLocal<String> = ThreadLocal()

    fun logic(name: String): String {
        logger.info("저장 name=$name -> nameStore=${nameStore.get()}")
        nameStore.set(name)
        Thread.sleep(1000)
        logger.info("조회 nameStore=${nameStore.get()}")
        return nameStore.get()
    }
}