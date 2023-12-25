package hello.advanced.app.v5

import hello.advanced.trace.callback.TraceTemplate
import hello.advanced.trace.logtrace.LogTrace
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Repository

@Repository
@RequiredArgsConstructor
class OrderRepositoryV5(
    private val template: TraceTemplate,
) {
    fun save(itemId: String) {
        template.execute("OrderRepository.save()") {
            if (itemId == "ex") {
                throw IllegalStateException("예외 발생!")
            }
            sleep(1000)
        }
    }

    private fun sleep(millis: Long) {
        Thread.sleep(millis)
    }
}
