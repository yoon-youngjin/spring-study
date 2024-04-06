package chapter10.exam08

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ScheduleWithFixedRateExample

fun main() {

    val scheduler = Executors.newScheduledThreadPool(3)

    val task = Runnable {
        Thread.sleep(2000)
        println("Thread: ${Thread.currentThread().name}")
    }
    val future1 = scheduler.scheduleWithFixedDelay(
        task, 1, 1, TimeUnit.SECONDS
    )
    val future2 = scheduler.scheduleWithFixedDelay(
        task, 1, 1, TimeUnit.SECONDS
    )
    val future3 = scheduler.scheduleWithFixedDelay(
        task, 1, 1, TimeUnit.SECONDS
    )
    Thread.sleep(10000)

    future1.cancel(true)
    future2.cancel(true)
    future3.cancel(true)
    scheduler.shutdown()
}