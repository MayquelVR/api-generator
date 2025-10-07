package com.viewdatatools.apigenarator.auth.domain.exception;

public class TokenHasExpiredException extends RuntimeException {
    public TokenHasExpiredException(String message) {
        super(message);
    }
}
