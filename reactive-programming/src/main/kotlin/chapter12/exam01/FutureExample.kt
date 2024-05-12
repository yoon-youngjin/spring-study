package chapter12.exam01

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

fun main() {
    val executorService = Executors.newFixedThreadPool(5)

    val future1: Future<Int> = executorService.submit(Service1())
    val future2: Future<Int> = executorService.submit(Service2(future1))
    val future3: Future<Int> = executorService.submit(Service3(future2))
    val future4: Future<Int> = executorService.submit(Service4(future3))
    val future5: Future<Int> = executorService.submit(Service5(future4))

    val finalResult = future5.get()

    executorService.shutdown()
    println("최종 결과: $finalResult")

}

class Service1 : Callable<Int> {
    override fun call(): Int {
        println("Service 1 시작")
        return 1
    }
}

class Service2(
    private val future: Future<Int>
) : Callable<Int> {
    override fun call(): Int {
        println("Service 2 시작")
        return future.get() + 2
    }
}

class Service3(
    private val future: Future<Int>
) : Callable<Int> {
    override fun call(): Int {
        println("Service 3 시작")
        return future.get() * 3
    }
}

class Service4(
    private val future: Future<Int>
) : Callable<Int> {
    override fun call(): Int {
        println("Service 4 시작")
        return future.get() - 4
    }
}

class Service5(
    private val future: Future<Int>
) : Callable<Int> {
    override fun call(): Int {
        println("Service 5 시작")
        return future.get() + 5
    }
}