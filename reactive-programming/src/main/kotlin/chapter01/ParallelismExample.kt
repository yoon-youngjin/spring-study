package chapter01

class ParallelismExample

fun main() {

    val cpuCores = Runtime.getRuntime().availableProcessors()

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

    println("CPU 개수만큼 데이터를 병렬로 처리하는 데 걸린 시간 ${endTime - startTime}ms")
    println("결과: $sum")
}