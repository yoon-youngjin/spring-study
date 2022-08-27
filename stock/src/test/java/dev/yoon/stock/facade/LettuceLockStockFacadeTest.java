package dev.yoon.stock.facade;

import dev.yoon.stock.domain.Stock;
import dev.yoon.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LettuceLockStockFacadeTest {

    @Autowired
    private LettuceLockStockFacade lettuceLockStockFacade;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach // 테스트 전 DB에 데이터 넣는 작업
    public void before() {
        Stock stock = new Stock(1L, 100L);

        stockRepository.saveAndFlush(stock);
    }

    @AfterEach // 테스트 후 자동으로 데이터 삭제
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    public void 동시에_100개의_요청_Lettuce() throws InterruptedException {

        int threadCount = 100;

        // ExecutorService: 비동기로 실행하는 작업을 단순하하여 사용할 수 있게 도와주는 자바의 API
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 100개의 요청이 끝날때까지 기다려야하므로 CountDownLatch를 사용
        // CountDownLatch: 다른 스레드에서 수행중인 작업이 모두 완료될 때까지 대기할 수 있도록 도와주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    lettuceLockStockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }

            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(0L, stock.getQuantity());


    }
}