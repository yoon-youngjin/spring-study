package dev.yoon.core.order;

import dev.yoon.core.member.Grade;
import dev.yoon.core.member.Member;
import dev.yoon.core.member.MemberService;
import dev.yoon.core.member.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class OrderServiceTest {


    private final MemberService memberService = new MemberServiceImpl();
    private final OrderService orderService = new OrderServiceImpl();

    @Test
    public void createOrder() throws Exception {
        // given
        Long memberId = 1L;
        Member member = new Member(memberId, "yoon", Grade.VIP);
        memberService.join(member);

        // when
        Order order = orderService.createOrder(memberId, "itemA", 10000);

        // then
        assertThat(order.getDiscountPrice()).isEqualTo(1000);
      
    }
}
