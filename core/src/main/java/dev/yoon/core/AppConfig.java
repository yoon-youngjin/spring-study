package dev.yoon.core;

import dev.yoon.core.discount.DiscountPolicy;
import dev.yoon.core.discount.RateDiscountPolicy;
import dev.yoon.core.member.MemberRepository;
import dev.yoon.core.member.MemberService;
import dev.yoon.core.member.MemberServiceImpl;
import dev.yoon.core.member.MemoryMemberRepository;
import dev.yoon.core.order.OrderService;
import dev.yoon.core.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MemberService memberService() {
        System.out.println("call AppConfig.memberService");
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public OrderService orderService() {
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(
                memberRepository(),
                discountPolicy()
        );
    }

    @Bean
    public MemberRepository memberRepository() {
        System.out.println("call AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }


}
