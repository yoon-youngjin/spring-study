package chapter11

import java.util.concurrent.LinkedBlockingQueue

class LinkedBlockingQueueExample

fun main() {
    val queue = LinkedBlockingQueue<Int>(5)

    val producer = Thread {
        for (i in 0..<10) {
            println("Producing $i")
            queue.put(i)
            Thread.sleep(10000)
        }
    }

    val consumer = Thread {
        for (i in 0..<10) {
            val data = queue.take()
            println("Consuming: $data")
            Thread.sleep(100) // 컨슈머가 더 빠르게 데이터를 소비하는 상황 가정
        }
    }

    producer.start()
    consumer.start()

    producer.join()
    consumer.join()
}
