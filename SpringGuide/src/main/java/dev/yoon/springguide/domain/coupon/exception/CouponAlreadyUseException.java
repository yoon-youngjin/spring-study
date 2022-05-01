package dev.yoon.springguide.domain.coupon.exception;


import dev.yoon.springguide.global.error.exception.BusinessException;
import dev.yoon.springguide.global.error.exception.ErrorCode;

public class CouponAlreadyUseException extends BusinessException {

  public CouponAlreadyUseException() {
    super(ErrorCode.COUPON_ALREADY_USE);
  }

}
