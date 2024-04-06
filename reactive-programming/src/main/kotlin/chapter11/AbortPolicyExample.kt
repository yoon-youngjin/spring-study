package chapter11

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class AbortPolicyExample

fun main() {
    val corePoolSize = 2
    val maximumPoolSize = 2
    val keepAliveTime = 0L
    val workQueueCapacity = 2

    val executor = ThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        TimeUnit.SECONDS,
        ArrayBlockingQueue(workQueueCapacity),
        ThreadPoolExecutor.AbortPolicy()
    )

    submitTasks(executor)
    executor.shutdown()
}

fun submitTasks(executor: ThreadPoolExecutor) {
    for (i in 0..4) {
        executor.submit {
            println("Task $i is running on thread ${Thread.currentThread().name}")
            Thread.sleep(1000)
        }
    }
}
