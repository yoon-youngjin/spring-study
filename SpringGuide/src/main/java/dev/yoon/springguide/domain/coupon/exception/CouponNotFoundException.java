package dev.yoon.springguide.domain.coupon.exception;


import javax.persistence.EntityNotFoundException;

public class CouponNotFoundException extends EntityNotFoundException {

    public CouponNotFoundException(String target) {
        super(target + "is not found");
    }
}
