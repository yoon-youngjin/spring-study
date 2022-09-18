package dev.yoon.core.order;

import dev.yoon.core.AppConfig;
import dev.yoon.core.discount.RateDiscountPolicy;
import dev.yoon.core.member.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class OrderServiceTest {


    private MemberService memberService;
    private OrderService orderService;

    @BeforeEach
    public void beforeEach() {
        AppConfig appConfig = new AppConfig();
        memberService = appConfig.memberService();
        orderService = appConfig.orderService();
    }

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
