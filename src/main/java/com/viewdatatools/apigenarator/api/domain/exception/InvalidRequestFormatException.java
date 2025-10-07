package com.viewdatatools.apigenarator.api.domain.exception;

public class InvalidRequestFormatException extends RuntimeException {
    public InvalidRequestFormatException(String message) {
        super(message);
    }
}

