package chapter11

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ThreadPoolExecutorHook

class CustomThreadPoolExecutor(
    corePoolSize: Int,
    maxPoolSize: Int,
    keepAliveTime: Long,
    queue: BlockingQueue<Runnable>
) : ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queue) {

    override fun beforeExecute(t: Thread?, r: Runnable?) {
        println("CustomThreadPoolExecutor.beforeExecute")
        super.beforeExecute(t, r)
    }

    override fun afterExecute(r: Runnable?, t: Throwable?) {
        println("CustomThreadPoolExecutor.afterExecute")
        if (t != null) {
            println("작업이 [${t.message}] 예외가 발생하였습니다.")
        } else {
            println("작업이 성공적으로 완료되었습니다.")
        }
        super.afterExecute(r, t)
    }

    override fun terminated() {
        println("CustomThreadPoolExecutor.terminated")
        super.terminated()
    }
}

fun main() {
    val corePoolSize = 2
    val maximumPoolSize = 2
    val keepAliveTime = 0L
    val workQueueCapacity = 2

    val executor = CustomThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        ArrayBlockingQueue(workQueueCapacity),
    )

    for (i in 0..4) {
        try {
            executor.submit {
                println("Task $i is running on thread ${Thread.currentThread().name}")
                Thread.sleep(1000)
            }
        } catch (e: RejectedExecutionException) {
            println(e)
        }
    }

    executor.shutdown()
}