package com.sprk.commons.exception;

import org.springframework.http.HttpStatus;

public class AnonymityException extends BaseException {
    public AnonymityException() {
        super(HttpStatus.NOT_FOUND);
    }
}
