package dev.yoon.jpa.controller;

import dev.yoon.jpa.exception.BaseException;
import dev.yoon.jpa.exception.ErrorResponseDto;
import dev.yoon.jpa.exception.PostNotInBoardException;
import dev.yoon.jpa.exception.PostNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("except")
public class ExceptTestController {
    @GetMapping("{id}")
    public void throwException(@PathVariable int id) {

        switch (id) {
            case 1:
                throw new PostNotExistException();
            case 2:
                throw new PostNotInBoardException();
            default:
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // @ExceptionHandler: 컨트롤러 내부에서 선언, 해당 함수는 지정된 예외에 대해서 처리
    // 해당 어노테이션 존재하는 컨트롤러에서만 가능
//    @ExceptionHandler(BaseException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST) // 어노테이션으로 response를 받거나 매개변수로 받을 수도 있다.
//    // 해당 어노테이션이 붙었으므로 들어오는 exception을 모두 BAD_REQUEST로 처리
//    public ErrorResponseDto handleBaseException(BaseException e, HttpServletResponse response) {
//
//        return new ErrorResponseDto(e.getMessage());
//
//    }
}
