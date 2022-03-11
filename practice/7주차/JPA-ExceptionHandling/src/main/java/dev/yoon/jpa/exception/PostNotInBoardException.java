package dev.yoon.jpa.exception;

// 해당 post가 board내에 존재하지 않을 경우 에러
public class PostNotInBoardException extends BaseException{

    public PostNotInBoardException() {
        super("Post not in board");
    }
}
