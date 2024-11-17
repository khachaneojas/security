package com.sprk.commons.exception;

import org.springframework.http.HttpStatus;

public class CaptchaFailedException extends BaseException {
    public CaptchaFailedException() {
        super(HttpStatus.UNAUTHORIZED, "Looks like you didn't pass the captcha test, the one that checks if you're not a robot.");
    }
    public CaptchaFailedException(String errorMessage) {
        super(HttpStatus.UNAUTHORIZED, errorMessage);
    }
}
