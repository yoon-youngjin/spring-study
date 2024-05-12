package chapter12.exam02

import java.util.concurrent.CompletableFuture

fun main() {
    val myService = MyService2()

    val cf: CompletableFuture<Void> = CompletableFuture.runAsync {
        println(Thread.currentThread().name + "가 비동기 작업을 시작합니다.")
        println(myService.getData())
    }

    cf.join()
}

class MyService2 {
    fun getData(): List<Int> {
        Thread.sleep(1000)
        return listOf(1, 2, 3)
    }
}