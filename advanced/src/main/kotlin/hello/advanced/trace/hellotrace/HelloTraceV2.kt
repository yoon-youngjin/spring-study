package hello.advanced.trace.hellotrace

import hello.advanced.trace.TraceId
import hello.advanced.trace.TraceStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

private val logger = LoggerFactory.getLogger("HelloTraceV2")

@Component
class HelloTraceV2 {
    fun begin(message: String): TraceStatus {
        val traceId = TraceId.create()
        val startTimeMs = System.currentTimeMillis()
        logger.info(
            "[{}] {}{}", traceId.id, addSpace(
                START_PREFIX,
                traceId.level
            ), message
        )
        return TraceStatus(traceId, startTimeMs, message)
    }

    fun beginSync(beforeTraceId: TraceId, message: String): TraceStatus {
        val nextId = beforeTraceId.createNextId()
        val startTimeMs = System.currentTimeMillis()
        logger.info(
            "[{}] {}{}", nextId.id, addSpace(
                START_PREFIX,
                nextId.level
            ), message
        )
        return TraceStatus(nextId, startTimeMs, message)
    }

    fun end(status: TraceStatus) {
        complete(status, null)
    }

    fun exception(status: TraceStatus?, e: Exception) {
        if (status == null) {
            throw IllegalArgumentException("status is not null")
        }
        complete(status, e)
    }

    private fun complete(status: TraceStatus, e: Exception?) {

        val stopTimeMs = System.currentTimeMillis()
        val resultTimeMs = stopTimeMs - status.startTimeMs
        val traceId = status.traceId
        if (e == null) {
            logger.info(
                "[{}] {}{} time={}ms", traceId.id,
                addSpace(
                    COMPLETE_PREFIX,
                    traceId.level
                ),
                status.message, resultTimeMs,
            )
        } else {
            logger.info(
                "[{}] {}{} time={}ms ex={}", traceId.id,
                addSpace(
                    EX_PREFIX,
                    traceId.level
                ),
                status.message, resultTimeMs, e.toString()
            )
        }
    }

    // level 0:
    // level 1: |-->
    // level 2: | |-->
    companion object {
        private const val START_PREFIX = "-->"
        private const val COMPLETE_PREFIX = "<--"
        private const val EX_PREFIX = "<X-"

        fun addSpace(prefix: String, level: Int): String {
            val sb = StringBuilder()
            for (i in 0 until level) {
                sb.append(if (i == level - 1) "|$prefix" else "| ")
            }
            return sb.toString()
        }
    }
}