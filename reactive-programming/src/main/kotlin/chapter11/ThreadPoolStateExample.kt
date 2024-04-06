package chapter11

import java.util.concurrent.Callable
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ThreadPoolStateExample

fun main() {
    val corePoolSize = 2
    val maximumPoolSize = 4
    val keepAliveTime = 0L
    val workQueueCapacity = 10

    val executor = ThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        TimeUnit.SECONDS,
        LinkedBlockingQueue(workQueueCapacity),
    )

    for (i in 1..4) {
        executor.submit(Callable {
            println("Task $i is running on thread ${Thread.currentThread().name}")
            Thread.sleep(1000)
            42
        })
    }

    executor.shutdown()
    println("========================================================")

    printThreadPoolState(executor)

    println("========================================================")

    // 스레드 풀이 종료될 때까지 대기 -> 1초를 기다려도 작업이 모두 종료 X
    val isTerminated = executor.awaitTermination(1, TimeUnit.SECONDS)

    if (!isTerminated) {
        executor.shutdownNow()
    }

    println("========================================================")

    while (!executor.isTerminated) {
        println("스레드 풀 종료 중...")
    }

    printThreadPoolState(executor)
    println("========================================================")
}

fun printThreadPoolState(executor: ThreadPoolExecutor) {
    if (executor.activeCount > 0) {
        println("ThreadPoolExecutor is RUNNING")
    }
    if (executor.isShutdown) {
        println("ThreadPoolExecutor is SHUTDOWN or STOP")
    }
    if (executor.isTerminating) {
        println("ThreadPoolExecutor is TIDYING")
    }
    if (executor.isTerminated) {
        println("ThreadPoolExecutor is TERMINATED")
    }
}
