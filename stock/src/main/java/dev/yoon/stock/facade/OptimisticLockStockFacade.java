package dev.yoon.stock.facade;

import dev.yoon.stock.service.OptimisticLockStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptimisticLockStockFacade {

    private final OptimisticLockStockService optimisticLockStockService;

    @Transactional
    public void decrease(Long id, Long quantity) throws InterruptedException {

        // update 실패 시 50ms 후 재실행하는 로직
        while (true) {
            try {
                optimisticLockStockService.decrease(id, quantity);
                break;
            }catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }

}
