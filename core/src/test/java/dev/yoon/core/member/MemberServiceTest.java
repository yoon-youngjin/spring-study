package dev.yoon.core.member;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MemberServiceTest {

    private final MemberService memberService = new MemberServiceImpl();
    
    @Test
    public void join() throws Exception {
        // given
        Member member = new Member(1L, "yoon", Grade.VIP);

        // when
        memberService.join(member);
        Member findMember = memberService.findMember(1L);

        // then
        Assertions.assertThat(member).isEqualTo(findMember);

    }


}
