package hello.aop.internalcall

import hello.aop.internalcall.aop.CallLogAspect
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(CallLogAspect::class)
@SpringBootTest
class CallServiceV2Test(
    @Autowired
    private val callServiceV2: CallServiceV2,
) {
    @Test
    fun external() {
        callServiceV2.external()
    }

    @Test
    fun internal() {
        callServiceV2.internal()
    }
}