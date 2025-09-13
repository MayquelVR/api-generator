package com.viewdatatools.apigenarator.auth.exception;

public class TokenHasExpiredException extends RuntimeException {
    public TokenHasExpiredException(String message) {
        super(message);
    }
}
