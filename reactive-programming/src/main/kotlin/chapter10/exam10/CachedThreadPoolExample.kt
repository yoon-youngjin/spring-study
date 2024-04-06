package chapter10.exam10

import java.util.concurrent.Executors

class CachedThreadPoolExample

fun main() {
    // newCachedThreadPool을 통해 생성된 스레드는 60초가 지나면 삭제된다.
    val executorService = Executors.newCachedThreadPool()

    for (i in 0..9) {
        executorService.submit {
            println("Task $i is executing on ${Thread.currentThread().name}")
        }
    }

    Thread.sleep(70000)
    executorService.shutdown()
}