package chapter10.exam09

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class FixedThreadPoolExample

class CustomThreadFactory : ThreadFactory {
    private var namePrefix: String = "customPool-" + POOL_NUMBER.getAndIncrement() + "-thread-"
    private val threadNumber = AtomicInteger(1)

    override fun newThread(r: Runnable): Thread {
        val threadName = namePrefix + threadNumber.getAndIncrement()
        println("thread create : $threadName")
        return Thread(
            r, threadName
        )
    }

    companion object {
        private val POOL_NUMBER = AtomicInteger(1)
    }
}

fun main() {
    val executorService1 = Executors.newFixedThreadPool(3, CustomThreadFactory())
    val executorService2 = Executors.newFixedThreadPool(3, CustomThreadFactory())

    for (i in 0..4) {
        executorService1.submit {
            Thread.sleep(1000)
            println("Thread ${Thread.currentThread().name}")
        }
        executorService2.submit {
            Thread.sleep(1000)
            println("Thread ${Thread.currentThread().name}")
        }
    }
    executorService1.shutdown()
    executorService2.shutdown()
}