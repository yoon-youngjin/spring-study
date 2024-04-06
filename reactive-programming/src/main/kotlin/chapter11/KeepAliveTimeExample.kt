package chapter11

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class KeepAliveTimeExample

fun main() {
    val corePoolSize = 2
    val maximumPoolSize = 4
    val keepAliveTime = 1L
    val workQueue = LinkedBlockingQueue<Runnable>(2)
    val executor = ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue)

    val taskNum = 6

    for (i in 0..<taskNum) {
        executor.execute {
            Thread.sleep(2000)
            println("${Thread.currentThread().name} 가 태스트 $i 를 실행하고 있습니다.")
        }
    }

//    executor.allowCoreThreadTimeOut(true) // corePool도 제거

    Thread.sleep(4000)
    executor.shutdown()
}