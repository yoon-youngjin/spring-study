package chapter10.exam06

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class AwaitTerminationExample

fun main() {
    val executorService = Executors.newFixedThreadPool(2) { r ->
        val thread = Thread(r)
        thread.isDaemon = true // 데몬 스레드는 주 스레드가 종료되면 자동으로 종료된다.
        thread
    }

    executorService.submit {
        while (true) {
            println("데몬 스레드 실행 중...")
            Thread.sleep(1000)
        }
    }

    executorService.shutdown()
// 데몬 스레드의 경우 주 스레드가 종료되는 즉시 종료되기 때문에 아무런 출력없이 종료된다.
// 반면 사용자 스레드의 경우에는 주 스레드에서 모든 스레드의 종료를 기다리기 때문에 현재 코드상에서 무한적으로 출력문이 찍힌다.
    executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)
    println("메인 스레드 종료")
}