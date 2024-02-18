package chapter01

class ConcurrencyExample

fun main() {
    val cpuCores = Runtime.getRuntime().availableProcessors() * 2
    // cpu 코어 개수보다 많도록 세팅하면 하나의 코어가 2가지 작업을 동시적으로 처리하기 때문에 기존보다 2배의 소요시간이 필요하다.
    // 만약 *3을 하면 하나의 코어가 3가지 작업을 하기 때문에 3배의 소요시간이 필요

    val data = mutableListOf<Int>()
    for (i in 0..<cpuCores) {
        data.add(i)
    }

    val startTime = System.currentTimeMillis()

    val sum = data.parallelStream()
        .mapToLong { i ->
            Thread.sleep(500)
            (i * i).toLong()
        }
        .sum()

    val endTime = System.currentTimeMillis()

    println("CPU 개수를 초과하는 데이터를 병렬로 처리하는 데 걸린 시간 ${endTime - startTime}ms")
    println("결과: $sum")
}