package hello.advanced.trace.threadlocal

import hello.advanced.trace.threadlocal.code.FieldService
import java.util.concurrent.CountDownLatch
import org.junit.jupiter.api.Test

internal class FieldServiceTest {
    private val fieldService = FieldService()

    @Test
    fun field() {
        val countDownLatch = CountDownLatch(2)

        val userA = Runnable {
            fieldService.logic("userA")
            countDownLatch.countDown()
        }
        val userB = Runnable {
            fieldService.logic("userB")
            countDownLatch.countDown()
        }


        val threadA = Thread(userA)
        threadA.name = "thread-A"
        val threadB = Thread(userB)
        threadA.name = "thread-B"

        threadA.start()
        Thread.sleep(100) // 동시성 방지
        threadB.start()

        countDownLatch.await() // 메인 스레드 대기
    }
}
