package dev.yoon.springguide.domain.coupon.dto;

import dev.yoon.springguide.domain.coupon.domain.Coupon;
import dev.yoon.springguide.domain.coupon.domain.CouponCode;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CouponResponse {

    private final CouponCode code;
    private final double discount;
    private final LocalDate expirationDate;
    private final boolean expiration;

    public CouponResponse(final Coupon coupon) {
        this.code = coupon.getCode();
        this.discount = coupon.getDiscount();
        this.expirationDate = coupon.getExpirationDate();
        this.expiration = coupon.isExpiration();
    }
}
