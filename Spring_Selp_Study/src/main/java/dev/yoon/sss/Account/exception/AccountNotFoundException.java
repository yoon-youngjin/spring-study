package dev.yoon.sss.Account.exception;

import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class AccountNotFoundException extends RuntimeException{
    private Long id;
    private Email email;

    public AccountNotFoundException(long id) {
        this.id = id;
    }

    public AccountNotFoundException(Email email) {
        this.email = email;
    }
}

