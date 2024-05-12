package chapter12.exam02

import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

fun main() {
    val myService = MyService()

//    val cf: CompletableFuture<List<Int>> = CompletableFuture.supplyAsync(object : Supplier<List<Int>> {
//        override fun get(): List<Int> {
//            println(Thread.currentThread().name + "가 비동기 작업을 시작합니다.")
//            return myService.getData()
//        }
//    })

    val cf: CompletableFuture<List<Int>> = CompletableFuture.supplyAsync {
        println(Thread.currentThread().name + "가 비동기 작업을 시작합니다.")
        myService.getData()
    }

    println(cf.get())
}

class MyService {
    fun getData(): List<Int> {
        Thread.sleep(1000)
        return listOf(1, 2, 3)
    }
}