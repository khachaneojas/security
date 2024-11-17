package com.sprk.commons.exception;

import org.springframework.http.HttpStatus;

public class DisposableEmailException extends BaseException {
    public DisposableEmailException(String email) {
        super(HttpStatus.BAD_REQUEST, "Hey, we noticed that your email '" + email + "' is one of those disposable ones. Sorry, but we can't move forward with it.");
    }
}
