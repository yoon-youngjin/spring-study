package chapter10.exam09

import java.util.concurrent.Executors

class SingleThreadExecutorExample

fun main() {
    val executor = Executors.newSingleThreadScheduledExecutor()

    for (i in 0..4) {
        Thread.sleep(1000)
        println("Task $i is executing on ${Thread.currentThread().name}")
    }
    executor.shutdown()
}