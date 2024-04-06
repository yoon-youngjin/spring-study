package chapter10.exam04

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

class FutureExample

fun main() {
    val executorService = Executors.newFixedThreadPool(1)

    val future: Future<Int> = executorService.submit(Callable {
        Thread.sleep(1000)
        42
    })

    val result = future.get()
    println("비동기 작업 결과 : $result")
    executorService.shutdown()
}