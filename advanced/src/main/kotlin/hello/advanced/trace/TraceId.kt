package hello.advanced.trace

import java.util.UUID

data class TraceId(
    val id: String,
    val level: Int,
) {

    fun createNextId(): TraceId {
        return TraceId(id, level + 1)
    }

    fun createPreviousId(): TraceId {
        return TraceId(id, level - 1)
    }

    fun isFirstLevel(): Boolean {
        return level == 0
    }

    companion object {
        fun create(): TraceId {
            val id = UUID.randomUUID().toString().substring(0, 8)
            return TraceId(id, 0)
        }
    }
}