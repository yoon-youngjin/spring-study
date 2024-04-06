package chapter11

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class DiscardPolicyExample

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
        ThreadPoolExecutor.DiscardPolicy()
    )

    submitTasks(executor)
    executor.shutdown()
}
