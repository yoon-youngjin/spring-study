package chapter10.exam04

import java.util.concurrent.Executors
import kotlin.coroutines.cancellation.CancellationException

class CallbackExample

interface Callback {
    fun onComplete(result: Int)
}

fun main() {
    val executorService = Executors.newFixedThreadPool(1)

    executorService.execute {
        Thread.sleep(1000)
        val result = 42
        val callback = object : Callback {
            override fun onComplete(result: Int) {
                println("비동기 작업 결과 : $result")
            }
        }
        callback.onComplete(result)
    }
    println("비동기 작업 시작")
    executorService.shutdown()
}