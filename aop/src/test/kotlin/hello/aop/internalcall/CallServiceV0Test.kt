package hello.aop.internalcall

import hello.aop.internalcall.aop.CallLogAspect
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

private val logger = LoggerFactory.getLogger(CallServiceV0Test::class.java)
@Import(CallLogAspect::class)
@SpringBootTest
internal class CallServiceV0Test(
    @Autowired
    private val callServiceV0: CallServiceV0,
) {
    @Test
    fun external() {
        callServiceV0.external()
    }

    @Test
    fun internal() {
        callServiceV0.internal()
    }
}