package chapter10.exam02

import java.util.concurrent.Executor

class SyncExecutor: Executor {
    override fun execute(command: Runnable) {
        command.run()
    }
}
fun main() {
    val executor = SyncExecutor()
    executor.execute {
        println("동기 작업 1 수행 중...")
        println("동기 작업 1 완료...")
    }

    executor.execute {
        println("동기 작업 2 수행 중...")
        println("동기 작업 2 완료...")
    }
}