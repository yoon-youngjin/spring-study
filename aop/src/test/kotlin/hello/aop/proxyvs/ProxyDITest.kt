package hello.aop.proxyvs

import hello.aop.member.MemberService
import hello.aop.member.MemberServiceImpl
import hello.aop.proxyvs.code.ProxyDIAspect
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

private val logger = LoggerFactory.getLogger(ProxyDITest::class.java)

@SpringBootTest(properties = ["spring.aop.proxy-target-class=true"]) // CGLIB 프록시
//@SpringBootTest(properties = ["spring.aop.proxy-target-class=false"]) // JDK 동적 프록시 -> 실패
@Import(ProxyDIAspect::class)
class ProxyDITest(
    @Autowired
    private val memberService: MemberService,
    @Autowired
    private val memberServiceImpl: MemberServiceImpl,
) {
    @Test
    fun go() {
        logger.info("memberService class=${memberService.javaClass}")
        logger.info("memberServiceImpl class=${memberServiceImpl.javaClass}")
        memberServiceImpl.hello("hello")
    }
}
