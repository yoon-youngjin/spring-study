package hello.aop.internalcall

import hello.aop.internalcall.aop.CallLogAspect
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(CallLogAspect::class)
@SpringBootTest
class CallServiceV1Test(
    @Autowired
    private val callServiceV1: CallServiceV1,
) {
    @Test
    fun external() {
        callServiceV1.external()
    }

    @Test
    fun internal() {
        callServiceV1.internal()
    }
}