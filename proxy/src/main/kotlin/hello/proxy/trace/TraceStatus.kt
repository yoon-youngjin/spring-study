package hello.advanced.trace

/**
 * 로그의 상태 정보를 나타내는 클래스
 */
data class TraceStatus(
    val traceId: TraceId,
    val startTimeMs: Long,
    val message: String
)
