package chapter11

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class PrestartThreadExample

fun main() {
    // corePoolSize 만큼 미리 쓰레드를 생성하는 전략
    val corePoolSize = 2
    val maximumPoolSize = 4
    val keepAliveTime = 0L
    val workQueue = LinkedBlockingQueue<Runnable>()
    val taskNum = 9

    val executor = ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue)
//    executor.prestartCoreThread() // 1개의 스레드만 생성
    executor.prestartAllCoreThreads() // corePoolSize 만큼 미리 쓰레드를 생성

    for (i in 0..<taskNum) {
        executor.execute {
            Thread.sleep(1000)
            println("${Thread.currentThread().name} 가 태스트 $i 를 실행하고 있습니다.")
        }
    }
    executor.shutdown()
}