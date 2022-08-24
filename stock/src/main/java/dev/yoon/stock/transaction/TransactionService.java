package dev.yoon.stock.transaction;

import dev.yoon.stock.domain.Stock;
import dev.yoon.stock.service.StockService;

public class TransactionService {

    private StockService stockService;

    public TransactionService(StockService stockService) {
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity) {

        startTransaction();

        stockService.decrease(id, quantity);

        endTransaction();

    }

    private void startTransaction() {

    }

    private void endTransaction() {

    }

}
