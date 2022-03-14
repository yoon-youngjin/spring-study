package dev.yoon.sss.delivery.service;

import dev.yoon.sss.delivery.dto.DeliveryDto;
import dev.yoon.sss.delivery.exception.DeliveryNotFoundException;
import dev.yoon.sss.delivery.repository.DeliveryRepository;
import dev.yoon.sss.delivery.domain.Delivery;
import dev.yoon.sss.delivery.domain.DeliveryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public Delivery create(DeliveryDto.CreationReq dto) {
        final Delivery delivery = dto.toEntity();
        delivery.addLog(DeliveryStatus.PENDING);
        return deliveryRepository.save(delivery);
    }

    public Delivery updateStatus(long id, DeliveryDto.UpdateReq dto) {
        final Delivery delivery = findById(id);
        delivery.addLog(dto.getStatus());
        return delivery;
    }


    public Delivery findById(long id) {
        final Optional<Delivery> delivery = deliveryRepository.findById(id);
        delivery.orElseThrow(() -> new DeliveryNotFoundException(id));
        return delivery.get();
    }

    public Delivery removeLogs(long id) {
        final Delivery delivery = findById(id);
        delivery.getLogs().clear();
        return delivery;
    }

    public void remove(long id) {
        deliveryRepository.deleteById(id);
    }


}
