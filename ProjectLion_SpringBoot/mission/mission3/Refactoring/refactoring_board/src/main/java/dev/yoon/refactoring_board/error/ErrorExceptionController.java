package dev.yoon.refactoring_board.error;

import dev.yoon.refactoring_board.exception.BoardNotFoundException;
import dev.yoon.refactoring_board.exception.NameDuplicationException;
import dev.yoon.refactoring_board.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorExceptionController {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<ErrorResponse.FieldError> fieldErrors = getFieldErrors(e.getBindingResult());
        return buildFieldErrors(ErrorCode.INPUT_VALUE_INVALID, fieldErrors);

    }

    @ExceptionHandler(value = {
            BoardNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse handleBoardNotFoundException(BoardNotFoundException e) {

        final ErrorCode accountNotFound = ErrorCode.BOARD_NOT_FOUND;
        log.error(accountNotFound.getMessage(), e.getMessage());
        return buildError(accountNotFound);
    }
    @ExceptionHandler(value = {
            UserNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse handleUserNotFoundException(UserNotFoundException e) {

        final ErrorCode accountNotFound = ErrorCode.ACCOUNT_NOT_FOUND;
        log.error(accountNotFound.getMessage(), e.getMessage());
        return buildError(accountNotFound);
    }

    @ExceptionHandler(NameDuplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleConstraintViolationException(NameDuplicationException e) {
        final ErrorCode errorCode = ErrorCode.EMAIL_DUPLICATION;
        log.error(errorCode.getMessage(), e.getName());
        return buildError(errorCode);
    }

//    @ExceptionHandler(value = {
//            AccountNotFoundException.class
//    })
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    protected ErrorResponse handleAccountNotFoundException(AccountNotFoundException e) {
//        final ErrorCode accountNotFound = ErrorCode.ACCOUNT_NOT_FOUND;
//        log.error(accountNotFound.getMessage(), e.getMessage());
//        return buildError(accountNotFound);
//    }

//    @ExceptionHandler(PasswordFailedExceededException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    protected ErrorResponseDto handlePasswordFailedExceededException(PasswordFailedExceededException e) {
//        log.error(e.getMessage());
//        return buildError(e.getErrorCode());
//    }

    private List<ErrorResponse.FieldError> getFieldErrors(BindingResult bindingResult) {
        final List<FieldError> errors = bindingResult.getFieldErrors();
        return errors.parallelStream()
                .map(error -> ErrorResponse.FieldError.builder()
                        .reason(error.getDefaultMessage())
                        .field(error.getField())
                        .value((String) error.getRejectedValue())
                        .build())
                .collect(Collectors.toList());
    }

    private ErrorResponse buildError(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .status(errorCode.getStatus())
                .message(errorCode.getMessage())
                .build();
    }

    private ErrorResponse buildFieldErrors(ErrorCode errorCode, List<ErrorResponse.FieldError> errors) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .status(errorCode.getStatus())
                .message(errorCode.getMessage())
                .errors(errors)
                .build();
    }
}
