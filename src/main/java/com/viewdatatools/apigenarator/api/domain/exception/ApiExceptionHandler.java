package com.viewdatatools.apigenarator.api.domain.exception;

import com.viewdatatools.apigenarator.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler  extends GlobalExceptionHandler {
    @ExceptionHandler(ApiNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleApiNotFound(ApiNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidHttpMethodException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidHttpMethod(InvalidHttpMethodException ex) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }

    @ExceptionHandler(InvalidRequestFormatException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequestFormat(InvalidRequestFormatException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
