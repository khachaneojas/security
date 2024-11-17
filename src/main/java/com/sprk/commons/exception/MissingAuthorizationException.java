package com.sprk.commons.exception;

import org.springframework.http.HttpStatus;

public class MissingAuthorizationException extends BaseException {
    public MissingAuthorizationException(String errorMessage) {
        super(HttpStatus.FORBIDDEN, errorMessage);
    }
    public MissingAuthorizationException() {
        super(HttpStatus.FORBIDDEN, "Required request header 'Authorization' is missing.");
    }
}
