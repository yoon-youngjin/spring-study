package dev.yoon.springguide.domain.coupon.application;

import dev.yoon.springguide.domain.coupon.dao.CouponFindDao;
import dev.yoon.springguide.domain.coupon.domain.Coupon;
import dev.yoon.springguide.domain.coupon.domain.CouponCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponUseService {

    private final CouponFindDao couponFindDao;

    public void use(final CouponCode code) {

        final Coupon coupon = couponFindDao.findByCode(code);
        coupon.use();
    }
}
