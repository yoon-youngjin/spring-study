package dev.yoon.core.order;

import dev.yoon.core.discount.DiscountPolicy;
import dev.yoon.core.discount.FixDiscountPolicy;
import dev.yoon.core.member.Member;
import dev.yoon.core.member.MemberRepository;
import dev.yoon.core.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService {

    MemberRepository memberRepository = new MemoryMemberRepository();
    DiscountPolicy discountPolicy = new FixDiscountPolicy();

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {


        Member findMember = memberRepository.findById(memberId);
        int discount = discountPolicy.discount(findMember, itemPrice);
        Order order = new Order(memberId, itemName, itemPrice, discount);

        return order;
    }
}
