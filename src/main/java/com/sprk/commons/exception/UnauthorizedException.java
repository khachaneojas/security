package com.sprk.commons.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException() {
        super(HttpStatus.FORBIDDEN, "Unauthorized access. You do not have the necessary permissions to access this resource.");
    }
    public UnauthorizedException(String errorMessage) {
        super(HttpStatus.FORBIDDEN, errorMessage);
    }
}
