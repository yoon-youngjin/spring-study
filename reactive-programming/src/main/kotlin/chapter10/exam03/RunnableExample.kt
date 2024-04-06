package chapter10.exam03

import java.util.concurrent.Executors

class RunnableExample

fun main() {
    val executorService = Executors.newFixedThreadPool(1) // 쓰레드풀 생성

    val runnableTask = Runnable {
        println("Runnbale 작업 수행 중..")
        println("Runnbale 작업 완료")
    }
    executorService.execute(runnableTask)
    executorService.shutdown()
}