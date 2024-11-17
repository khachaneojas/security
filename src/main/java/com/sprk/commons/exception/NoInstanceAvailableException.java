package com.sprk.commons.exception;

import org.springframework.http.HttpStatus;

public class NoInstanceAvailableException extends BaseException {
    public NoInstanceAvailableException(String errorMessage) {
        super(HttpStatus.BAD_REQUEST, errorMessage);
    }
    public NoInstanceAvailableException(Exception exception) {
        super(HttpStatus.BAD_REQUEST, exception, "Service unavailable at the moment.");
    }
    public NoInstanceAvailableException(Exception exception, String errorMessage) {
        super(HttpStatus.BAD_REQUEST, exception, errorMessage);
    }
}
