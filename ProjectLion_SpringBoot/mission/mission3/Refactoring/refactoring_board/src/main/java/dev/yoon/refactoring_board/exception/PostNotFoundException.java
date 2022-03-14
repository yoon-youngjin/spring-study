package dev.yoon.refactoring_board.exception;

public class PostNotFoundException extends RuntimeException {
    private Long id;

    public PostNotFoundException(Long id) {
        this.id = id;
    }
}
