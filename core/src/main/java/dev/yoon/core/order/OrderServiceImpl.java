package dev.yoon.core.order;

import dev.yoon.core.annotation.MainDiscountPolicy;
import dev.yoon.core.discount.DiscountPolicy;
import dev.yoon.core.member.Member;
import dev.yoon.core.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository,  DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    //
//    @Autowired
//    @Qualifier("mainDiscountPolicy")
//    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
//        this.discountPolicy = discountPolicy;
//    }

//    @Autowired
//    public void init(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
//        this.memberRepository = memberRepository;
//        this.discountPolicy = discountPolicy;
//    }

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
