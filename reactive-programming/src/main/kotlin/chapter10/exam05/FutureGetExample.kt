package chapter10.exam05

import java.util.concurrent.Callable
import java.util.concurrent.Executors

class FutureGetExample

fun main() {
    val executorService = Executors.newFixedThreadPool(1)

    val callableTask = Callable {
        println("비동기 작업 시작..")
        Thread.sleep(2000)
        println("비동기 작업 완료")
        42
    }

    val future = executorService.submit(callableTask)

    while (!future.isDone) {
        println("작업을 기다리는 중...")
        Thread.sleep(500)
    }

//    println("result = ${future.get()}")
    executorService.shutdown()
}