package dev.yoon.core.discount;

import dev.yoon.core.member.Member;

public interface DiscountPolicy {

    /**
     * @return 할인 대상 금액
     */
    // f2 -> 오류 위치로 이동
    int discount(Member member, int price);
}
