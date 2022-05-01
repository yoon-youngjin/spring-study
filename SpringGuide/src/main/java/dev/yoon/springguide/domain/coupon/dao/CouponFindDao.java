package dev.yoon.springguide.domain.coupon.dao;

import dev.yoon.springguide.domain.coupon.domain.Coupon;
import dev.yoon.springguide.domain.coupon.domain.CouponCode;
import dev.yoon.springguide.domain.coupon.exception.CouponNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponFindDao {

    private final CouponRepository couponRepository;

    public Coupon findByCode(final CouponCode code) {
        final Optional<Coupon> coupon = couponRepository.findByCode(code);
        coupon.orElseThrow(() -> new CouponNotFoundException(code.getValue()));
        return coupon.get();

    }
}
