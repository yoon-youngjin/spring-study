package chapter10.exam07

import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

class InvokeAllExample

fun main() {
    val executorService = Executors.newFixedThreadPool(3)
    val tasks = mutableListOf<Callable<Int>>()
    tasks.add(Callable {
        Thread.sleep(3000)
        1
    })
    tasks.add(Callable {
        Thread.sleep(2000)
        2
    })
    tasks.add(Callable {
        Thread.sleep(1000)
        throw RuntimeException("invokAll")
    })

    val totalExecutionTime = measureTimeMillis {
        val results = executorService.invokeAll(tasks)
        for (result in results) {
            val done = result.isDone // 정상이든 예외든 모든 결과는 종료이므로 true
            try {
                val value = result.get() // 작업이 담긴 순서대로 받을 수 있다.
                println("Task result: $value")
            } catch (e: ExecutionException) { // 3번째 작업을 get할때 ExecutionException이 던져진다.
                e.printStackTrace()
            }
        }
    }
    println("총 소요시간: $totalExecutionTime")
    executorService.shutdown()
}