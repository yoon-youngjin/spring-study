package dev.yoon.stock.service;

import dev.yoon.stock.domain.Stock;
import dev.yoon.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    // 재고 감소
//    @Transactional
//    public synchronized void decrease(Long id, Long quantity) {
//        // get stock
//        // 재고감소
//        // 저장
//
//        Stock stock = stockRepository.findById(id).orElseThrow();
//
//        stock.decrease(quantity);
//
//        stockRepository.saveAndFlush(stock);
//
//    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(Long id, Long quantity) {
        // get stock
        // 재고감소
        // 저장

        Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);

    }

}
