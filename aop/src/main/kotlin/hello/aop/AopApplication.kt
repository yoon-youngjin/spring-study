package hello.aop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AopApplication

fun main(args: Array<String>) {
    runApplication<AopApplication>(*args)
}
