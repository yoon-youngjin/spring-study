package chapter10.exam10

import java.util.concurrent.Executors

class CachedThreadPool60Example

fun main() {
    // newCachedThreadPool을 통해 생성된 스레드는 60초가 지나면 삭제된다.
    val executorService = Executors.newCachedThreadPool()

    for (i in 0..4) {
        executorService.submit {
            Thread.sleep(1000)
            println("Thread ${Thread.currentThread().name}")
        }
    }
    executorService.shutdown()
}