package dev.yoon.stock.facade;

import dev.yoon.stock.repository.LockRepository;
import dev.yoon.stock.service.OptimisticLockStockService;
import dev.yoon.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NamedLockStockFacade {

    private final LockRepository lockRepository;

    private final StockService stockService;


    @Transactional
    public void decrease(Long id, Long quantity) throws InterruptedException {

        try {
            lockRepository.getLock(id.toString());
            stockService.decrease(id, quantity);
        }finally {
            lockRepository.releaseLock(id.toString());

        }
    }

}
