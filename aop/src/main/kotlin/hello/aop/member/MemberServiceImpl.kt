package hello.aop.member

import hello.aop.member.annotation.ClassAop
import hello.aop.member.annotation.MethodAop
import org.springframework.stereotype.Component

@ClassAop
@Component
class MemberServiceImpl : MemberService {
    @MethodAop("test value")
    override fun hello(param: String): String {
        return "ok"
    }

    fun internal(param: String): String {
        return "ok"
    }
}