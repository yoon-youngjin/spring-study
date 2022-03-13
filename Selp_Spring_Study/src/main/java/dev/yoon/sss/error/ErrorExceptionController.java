package dev.yoon.sss.error;

import dev.yoon.sss.Account.exception.AccountNotFoundException;
import dev.yoon.sss.Account.exception.PasswordFailedExceededException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorExceptionController {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<ErrorResponseDto.FieldError> fieldErrors = getFieldErrors(e.getBindingResult());
        return buildFieldErrors(ErrorCode.INPUT_VALUE_INVALID, fieldErrors);

    }

    @ExceptionHandler(value = {
            AccountNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponseDto handleAccountNotFoundException(AccountNotFoundException e) {
        final ErrorCode accountNotFound = ErrorCode.ACCOUNT_NOT_FOUND;
        log.error(accountNotFound.getMessage(), e.getMessage());
        return buildError(accountNotFound);
    }

    @ExceptionHandler(PasswordFailedExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponseDto handlePasswordFailedExceededException(PasswordFailedExceededException e) {
        log.error(e.getMessage());
        return buildError(e.getErrorCode());
    }

    private List<ErrorResponseDto.FieldError> getFieldErrors(BindingResult bindingResult) {
        final List<FieldError> errors = bindingResult.getFieldErrors();
        return errors.parallelStream()
                .map(error -> ErrorResponseDto.FieldError.builder()
                        .reason(error.getDefaultMessage())
                        .field(error.getField())
                        .value((String) error.getRejectedValue())
                        .build())
                .collect(Collectors.toList());
    }

    private ErrorResponseDto buildError(ErrorCode errorCode) {
        return ErrorResponseDto.builder()
                .code(errorCode.getCode())
                .status(errorCode.getStatus())
                .message(errorCode.getMessage())
                .build();
    }

    private ErrorResponseDto buildFieldErrors(ErrorCode errorCode, List<ErrorResponseDto.FieldError> errors) {
        return ErrorResponseDto.builder()
                .code(errorCode.getCode())
                .status(errorCode.getStatus())
                .message(errorCode.getMessage())
                .errors(errors)
                .build();
    }
}
