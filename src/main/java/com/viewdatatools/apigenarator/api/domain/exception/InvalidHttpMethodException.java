package com.viewdatatools.apigenarator.api.domain.exception;

public class InvalidHttpMethodException extends RuntimeException {
    public InvalidHttpMethodException(String message) {
        super(message);
    }
}

