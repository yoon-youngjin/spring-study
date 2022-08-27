package dev.yoon.stock.facade;

import dev.yoon.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockStockFacade {

    private final RedissonClient redissonClient;

    private final StockService stockService;

    public void decrease(Long key, Long quantity) throws InterruptedException {
        RLock lock = redissonClient.getLock(key.toString()); // key를 통해 Rock 객체 획득

        try {
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);// Rock 획득 시도, 몇 초동안 Lock 획득을 시도할 것인지 ? 몇 초동안 점유할 것인지?

            if (!available) {
                System.out.println("Lock 획득 실패");
                return;
            }
            stockService.decrease(key, quantity);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

}
