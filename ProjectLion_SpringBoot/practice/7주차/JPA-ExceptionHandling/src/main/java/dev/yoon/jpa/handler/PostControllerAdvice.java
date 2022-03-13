package dev.yoon.jpa.handler;

import dev.yoon.jpa.exception.BaseException;
import dev.yoon.jpa.exception.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
// @ControllerAdvice: 모든 ExceptionHandler를 가져옴
// 컨트롤러에 종속적이지 않음
public class PostControllerAdvice {

    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleException(BaseException e) {
        return new ErrorResponseDto(e.getMessage());
    }

    //Validation에 대한 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseDto handleValidException(
            MethodArgumentNotValidException e
    ){
        return new ErrorResponseDto(e.getMessage());
    }

}
