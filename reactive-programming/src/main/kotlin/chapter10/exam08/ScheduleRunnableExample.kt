package chapter10.exam08

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ScheduleRunnableExample

fun main() {

    val scheduledExecutorService = Executors.newScheduledThreadPool(1)

    val task = Runnable {
        println("작업이 한 번 실행되고 결과를 반환한다.")
    }
    val future = scheduledExecutorService.schedule(
        task, 3, TimeUnit.SECONDS
    )

    Thread.sleep(5000)
    val result = future.get()
    println("result = $result")
    scheduledExecutorService.shutdown()
}