package chapter10.exam09

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ScheduledThreadPoolExample

fun main() {
    val executor = Executors.newScheduledThreadPool(1)
    val task = Runnable {
        println("Task is running ...")
    }

    val initialDelay = 0L // 초기 지연 X
    val initialPeriod = 1L // 초기 주기
    val updatePeriod = 3L // 변경된 주기

    var future = executor.scheduleAtFixedRate(task, initialDelay, initialPeriod, TimeUnit.SECONDS)

    Thread.sleep(5000)
    future.cancel(true) // 실행중이더라도 바로 취소

    future = executor.scheduleAtFixedRate(task, 0, updatePeriod, TimeUnit.SECONDS) // 주기 변경
    Thread.sleep(50000)

    future.cancel(false) // 실행중이라면 완료되고 취소
    executor.shutdown()
}