package com.project.quartz.util;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Created by user on 3:38 21/04/2025, 2025
 */
public class CustomException extends Exception {

    @Serial
    private static final long serialVersionUID = 6573126493208180437L;
    private final String message;
    private final HttpStatus httpStatus;

    public CustomException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
