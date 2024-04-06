package chapter10.exam03

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

class CallableExample

fun main() {
    val executorService = Executors.newFixedThreadPool(1)

    val callableTask = Callable<Int> {
        println("Callable 작업 수행중..")
        println("Callable 작업 완료")
        42
    }
    val future: Future<Int> = executorService.submit(callableTask)
    println("${Thread.currentThread()} + Callable 작업 결과 :  ${future.get()}")

    executorService.shutdown()
}