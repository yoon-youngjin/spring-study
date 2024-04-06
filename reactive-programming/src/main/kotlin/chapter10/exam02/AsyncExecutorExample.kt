package chapter10.exam02

import java.util.concurrent.Executor

class AsyncExecutor : Executor {
    override fun execute(command: Runnable) {
        Thread(command).start()
    }
}

fun main() {
    val executor = AsyncExecutor()
    executor.execute {
        println("비동기 작업 1 수행 중...")
        println("비동기 작업 1 완료...")
    }

    executor.execute {
        println("비동기 작업 2 수행 중...")
        println("비동기 작업 2 완료...")
    }
}