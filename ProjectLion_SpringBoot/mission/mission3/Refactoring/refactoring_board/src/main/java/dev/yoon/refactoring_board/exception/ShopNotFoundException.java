package dev.yoon.refactoring_board.exception;

public class ShopNotFoundException extends RuntimeException {

    private Long id;

    public ShopNotFoundException(Long shopId) {
        this.id = shopId;
    }
}
