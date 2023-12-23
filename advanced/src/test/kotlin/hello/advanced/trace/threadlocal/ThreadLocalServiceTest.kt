package hello.advanced.trace.threadlocal

import hello.advanced.trace.threadlocal.code.ThreadLocalService
import java.util.concurrent.CountDownLatch
import org.junit.jupiter.api.Test

internal class ThreadLocalServiceTest {
    private val threadLocalService = ThreadLocalService()

    @Test
    fun field() {
        val countDownLatch = CountDownLatch(2)

        val userA = Runnable {
            threadLocalService.logic("userA")
            countDownLatch.countDown()
        }
        val userB = Runnable {
            threadLocalService.logic("userB")
            countDownLatch.countDown()
        }


        val threadA = Thread(userA)
        threadA.name = "thread-A"
        val threadB = Thread(userB)
        threadB.name = "thread-B"

        threadA.start()
        threadB.start()

        countDownLatch.await() // 메인 스레드 대기
    }
}
