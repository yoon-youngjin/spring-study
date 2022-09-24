package dev.yoon.core.order;

import dev.yoon.core.discount.FixDiscountPolicy;
import dev.yoon.core.member.Grade;
import dev.yoon.core.member.Member;
import dev.yoon.core.member.MemoryMemberRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceImplTest {

    @Test
    public void createOrder() throws Exception {
        MemoryMemberRepository memberRepository = new MemoryMemberRepository();
        memberRepository.save(new Member(1L, "Yoon", Grade.VIP));
        OrderServiceImpl orderService = new OrderServiceImpl(memberRepository, new FixDiscountPolicy());
        orderService.createOrder(1L, "itemA", 10000);

    }

}