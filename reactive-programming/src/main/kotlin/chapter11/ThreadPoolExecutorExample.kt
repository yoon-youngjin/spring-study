package chapter11

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ThreadPoolExecutorExample

fun main() {
    val corePoolSize = 2
    val maximumPoolSize = 4
    val keepAliveTime = 0L // corePoolSize를 제외한 나머지 유휴 상태의 스레드를 제거하는데 까지 기다리는 시간
//    val workQueue = LinkedBlockingQueue<Runnable>() // 개수 제한이 없으므로 maximumPoolSize 만큼 스레드 풀이 생성될 수 없다.
    val workQueue = ArrayBlockingQueue<Runnable>(4)
//    val taskNum = 7 // corePoolSize + QueueSize = 6 이므로 task 개수가 1만큼 더 크기 때문에 쓰레드를 하나 추가한다. -> 2개의 corePool 쓰레드가 작업 2개를 가져가고, 나머지 4개의 작업이 작업 큐에 쌓여있는데, 마지막 작업이 큐에 못들어오는 상황이므로 스레드를 생성
//    val taskNum = 8 // corePoolSize + QueueSize = 6 이므로 task 개수가 2만큼 더 크기 때문에 쓰레드를 두개를 추가한다.
    val taskNum = 9 // maximumPoolSize 만큼 스레드를 늘려도 하나의 작업이 큐에 들어오지 못하므로 에러가 발생한다. -> handler를 정의해서 처리할 수 있다.
    val executor = ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue)

    for (i in 0..<taskNum) {
        executor.execute {
            Thread.sleep(1000)
            println("${Thread.currentThread().name} 가 태스트 $i 를 실행하고 있습니다.")
        }
    }
    executor.shutdown()
}
