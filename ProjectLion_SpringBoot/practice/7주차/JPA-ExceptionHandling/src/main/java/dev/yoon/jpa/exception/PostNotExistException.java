package dev.yoon.jpa.exception;

// post존재 안 할 경우 에러
public class PostNotExistException extends BaseException {

    public PostNotExistException() {
        super("target Post does not exist");
    }
}
