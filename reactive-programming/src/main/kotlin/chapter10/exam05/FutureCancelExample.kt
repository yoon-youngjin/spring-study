package chapter10.exam05

import java.util.concurrent.Callable
import java.util.concurrent.Executors

class FutureCancelExample

fun main() {
    val executorService = Executors.newFixedThreadPool(1)

    val callableTask = Callable {
        println("비동기 작업 시작..")
        Thread.sleep(2000)
        println("비동기 작업 완료")
        42
    }

    val future = executorService.submit(callableTask)
    Thread.sleep(1000) // 비동기 시작하자마자 cancel을 주면 시작도 못하기 때문에 잠깐의 sleep
//    future.cancel(true) // 비동기 작업 완료 출력 X
    future.cancel(false) // 비동기 작업 완료 출력 O

    if (!future.isCancelled) {
        runCatching {
            println("result = ${future.get()}")
        }.onFailure {
            println(it)
        }.also {
            executorService.shutdown()
        }
    } else {
        println("작업이 취소되었습니다.")
    }


}