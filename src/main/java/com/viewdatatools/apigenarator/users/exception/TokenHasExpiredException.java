package com.viewdatatools.apigenarator.users.exception;

public class TokenHasExpiredException extends RuntimeException {
    public TokenHasExpiredException(String message) {
        super(message);
    }
}
