package hello.advanced.app.v4.kv

import hello.advanced.trace.logtrace.LogTrace
import hello.advanced.trace.template.AbstractTemplate
import hello.advanced.trace.template.TemplatePattern
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Repository

@Repository
@RequiredArgsConstructor
class OrderRepositoryV4_1(
    trace: LogTrace,
) {
    private val template = TemplatePattern<Unit>(trace)

    fun save(itemId: String) {
        template.execute(
            message = "OrderRepository.save()",
            call = {
                if (itemId == "ex") {
                    throw IllegalStateException("예외 발생!")
                }
                sleep(1000)
            }
        )
    }

    private fun sleep(millis: Long) {
        Thread.sleep(millis)
    }
}
