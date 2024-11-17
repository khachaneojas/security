package com.sprk.commons.exception;

import org.springframework.http.HttpStatus;

public class DataAlreadyExistException extends BaseException {
    public DataAlreadyExistException() {
        super(HttpStatus.BAD_REQUEST, "Data already exists.");
    }
    public DataAlreadyExistException(String errorMessage) {
        super(HttpStatus.BAD_REQUEST, errorMessage);
    }
}
