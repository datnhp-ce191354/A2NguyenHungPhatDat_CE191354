package com.fu.news.exception;

import org.springframework.http.HttpStatus;

/** A business-rule error that should become an HTTP response with a readable message. */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
