package dev.yoon.sss.delivery.repository;

import dev.yoon.sss.delivery.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
