package dev.yoon.springguide.domain.coupon.dao;


import dev.yoon.springguide.domain.coupon.domain.Coupon;
import dev.yoon.springguide.domain.coupon.domain.CouponCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(CouponCode code);


}
