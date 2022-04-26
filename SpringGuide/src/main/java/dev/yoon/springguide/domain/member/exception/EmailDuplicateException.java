package dev.yoon.springguide.domain.member.exception;


import dev.yoon.springguide.domain.member.model.Email;
import dev.yoon.springguide.global.error.exception.ErrorCode;
import dev.yoon.springguide.global.error.exception.InvalidValueException;

public class EmailDuplicateException extends InvalidValueException {

    public EmailDuplicateException(final Email email) {
        super(email.getValue(), ErrorCode.EMAIL_DUPLICATION);
    }
}
