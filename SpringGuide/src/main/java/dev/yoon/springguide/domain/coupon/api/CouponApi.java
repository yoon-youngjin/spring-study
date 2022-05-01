package dev.yoon.springguide.domain.coupon.api;

import dev.yoon.springguide.domain.coupon.application.CouponUseService;
import dev.yoon.springguide.domain.coupon.dao.CouponFindDao;
import dev.yoon.springguide.domain.coupon.domain.CouponCode;
import dev.yoon.springguide.domain.coupon.dto.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponApi {

    private final CouponFindDao couponFindDao;
    private final CouponUseService couponUseService;

    @GetMapping("/{code}")
    public CouponResponse getCoupon(@PathVariable final String code) {
        return new CouponResponse(couponFindDao.findByCode(CouponCode.of(code)));
    }

    @PutMapping("/{code}")
    public void useCoupon(@PathVariable final String code) {
        couponUseService.use(CouponCode.of(code));

    }
}
