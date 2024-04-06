package chapter10.exam07

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

class InvokeAnyExample

fun main() {
    val executorService = Executors.newFixedThreadPool(3)
    val tasks = mutableListOf<Callable<String>>()
    tasks.add(Callable {
        Thread.sleep(2000)
        "Task 1"
    })
    tasks.add(Callable {
        Thread.sleep(1000)
        throw RuntimeException("error")
    })
    tasks.add(Callable {
        Thread.sleep(3000)
        "Task 3"
    })
    val totalExecutionTime = measureTimeMillis {
        runCatching {
            val result = executorService.invokeAny(tasks)
            println("First completed task result: $result") // Task 2가 가장 빠르게 끝났지만 예외가 발생했으므로 건너뛴다.
        }.also {
            executorService.shutdown()
        }.onFailure {
            it.printStackTrace()
        }
    }
    println("총 소요시간: $totalExecutionTime")
}
