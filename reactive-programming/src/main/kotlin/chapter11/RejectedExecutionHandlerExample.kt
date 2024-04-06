package chapter11

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class RejectedExecutionHandlerExample

class CustomRejectedExecutionHandler : RejectedExecutionHandler {
    override fun rejectedExecution(r: Runnable?, executor: ThreadPoolExecutor) {
        println("태스크가 거부되었습니다.")
        if (!executor.isShutdown) {
            executor.queue.poll()
            executor.queue.offer(r)
        }
    }
}

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
        CustomRejectedExecutionHandler()
    )

    submitTasks(executor)
    executor.shutdown()
}