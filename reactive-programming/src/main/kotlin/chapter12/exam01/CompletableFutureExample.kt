package chapter12.exam01

import java.util.concurrent.CompletableFuture

fun main() {
    val finalResult = CompletableFuture.supplyAsync {
        println("Service 1 시작")
        return@supplyAsync 1
    }.thenApplyAsync { result1 ->
        println("Service 2 시작")
        return@thenApplyAsync result1 + 2
    }.thenApplyAsync { result2 ->
        println("Service 3 시작")
        return@thenApplyAsync result2 * 3
    }.thenApplyAsync { result3 ->
        println("Service 4 시작")
        return@thenApplyAsync result3 - 4
    }.thenApplyAsync { result4 ->
        println("Service 5 시작")
        return@thenApplyAsync result4 + 5
    }.get()

    println("최종 결과: $finalResult")
}
