package dev.yoon.springguide.domain.member.exception;


import dev.yoon.springguide.domain.member.model.Email;
import dev.yoon.springguide.global.error.exception.BusinessException;
import dev.yoon.springguide.global.error.exception.ErrorCode;

public class EmailDuplicateException extends BusinessException {

    public EmailDuplicateException(final Email email) {
        super(email.getValue(), ErrorCode.EMAIL_DUPLICATION);
    }
}
