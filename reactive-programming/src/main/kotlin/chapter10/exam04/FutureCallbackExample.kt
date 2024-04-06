package chapter10.exam04

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

class FutureCallbackExample

fun main() {
    val executorService = Executors.newFixedThreadPool(1)

    val callableTask = Callable {
        Thread.sleep(1000)
        42
    }
    val future: Future<Int> = executorService.submit(callableTask)
    println("비동기 작업 시작")
    registerCallback(future, object : Callback {
        override fun onComplete(result: Int) {
            println("비동기 작업 결과: $result")
        }
    })
    executorService.shutdown()
}

fun registerCallback(future: Future<Int>, callback: Callback) {
    Thread {
        val result = future.get()
        callback.onComplete(result)
    }.start()
}
