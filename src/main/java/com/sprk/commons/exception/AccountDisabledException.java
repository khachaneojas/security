package com.sprk.commons.exception;

import org.springframework.http.HttpStatus;

public class AccountDisabledException extends BaseException {
    public AccountDisabledException(String errorMessage) {
        super(HttpStatus.FORBIDDEN, errorMessage);
    }
}
