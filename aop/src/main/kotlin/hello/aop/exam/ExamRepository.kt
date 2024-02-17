package hello.aop.exam

import hello.aop.exam.annotation.Retry
import hello.aop.exam.annotation.Trace
import org.springframework.stereotype.Repository

@Repository
class ExamRepository {
    private var seq = 0

    /**
     * 5번에 1번 실패하는 요청
     */
    @Trace
    @Retry(4)
    fun save(itemId: String): String {
        seq++
        if (seq % 5 == 0) {
            throw IllegalStateException("예외 발생")
        }
        return "ok"
    }
}
