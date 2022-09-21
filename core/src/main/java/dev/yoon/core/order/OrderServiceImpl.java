package dev.yoon.core.order;

import dev.yoon.core.discount.DiscountPolicy;
import dev.yoon.core.member.Member;
import dev.yoon.core.member.MemberRepository;

public class OrderServiceImpl implements OrderService {

    private MemberRepository memberRepository;
    private DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {


        Member findMember = memberRepository.findById(memberId);
        int discount = discountPolicy.discount(findMember, itemPrice);
        Order order = new Order(memberId, itemName, itemPrice, discount);

        return order;
    }

    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
