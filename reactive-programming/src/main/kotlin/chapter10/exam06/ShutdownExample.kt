package chapter10.exam06

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ShutdownExample

fun main() {
    val executorService = Executors.newFixedThreadPool(2)
    for (i in 0..4) {
        executorService.submit(Callable {
            try {
                Thread.sleep(1000)
                println("${Thread.currentThread().name}: 작업 종료")
            } catch (e: InterruptedException) {
//                인터럽트가 걸려서 예외를 처리하게 되면 인터럽트 상태가 쓰레드마다 초기화된다.(초기화된 인터럽트 상태 원복)
//                스레드 풀에서 인터럽트 상태를 확인하는 부분이 존재하기 때문에
                Thread.currentThread().interrupt()
                throw RuntimeException("인터럽트 걸림")
            }
            42
        })
    }

    executorService.shutdown()

    try {
        if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
            executorService.shutdownNow()
            println("스레드 풀 강제 종료 수행...")
        }
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
        throw RuntimeException(e)
    }

    println("스레드 풀 종료 여부: ${executorService.isShutdown}")

    while (!executorService.isTerminated) {
        println("스레드 풀 종료 중..")
    }
    println("스레드 풀 종료 완료")
}