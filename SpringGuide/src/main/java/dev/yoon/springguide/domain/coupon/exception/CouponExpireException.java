package dev.yoon.springguide.domain.coupon.exception;


import dev.yoon.springguide.global.error.exception.BusinessException;
import dev.yoon.springguide.global.error.exception.ErrorCode;

public class CouponExpireException extends BusinessException {

  public CouponExpireException() {
    super(ErrorCode.COUPON_EXPIRE);
  }
}
