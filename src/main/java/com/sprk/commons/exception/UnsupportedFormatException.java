package com.sprk.commons.exception;

import org.springframework.http.HttpStatus;

public class UnsupportedFormatException extends BaseException {
    public UnsupportedFormatException(String errorMessage) {
        super(HttpStatus.BAD_REQUEST, errorMessage);
    }
    public UnsupportedFormatException(Exception exception) {
        super(HttpStatus.BAD_REQUEST, exception);
    }
    public UnsupportedFormatException(Exception exception, String errorMessage) {
        super(HttpStatus.BAD_REQUEST, exception, errorMessage);
    }
}
