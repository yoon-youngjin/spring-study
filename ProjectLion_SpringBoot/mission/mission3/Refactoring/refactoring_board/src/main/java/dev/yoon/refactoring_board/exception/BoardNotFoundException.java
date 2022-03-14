package dev.yoon.refactoring_board.exception;

public class BoardNotFoundException extends RuntimeException {
    private Long id;

    public BoardNotFoundException(Long id) {
        this.id = id;
    }
}
