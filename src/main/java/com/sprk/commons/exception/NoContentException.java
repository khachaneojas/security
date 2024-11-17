package com.sprk.commons.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class NoContentException extends BaseException {
    public NoContentException() {
        super(HttpStatus.OK, List.of());
    }
    public NoContentException(String errorMessage) {
        super(HttpStatus.OK, errorMessage, List.of());
    }
}
