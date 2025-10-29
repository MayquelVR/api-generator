package com.viewdatatools.apigenarator.exception;

import com.viewdatatools.apigenarator.collection.domain.exception.CollectionNotFoundException;
import com.viewdatatools.apigenarator.collection.domain.exception.InvalidSchemaException;
import com.viewdatatools.apigenarator.collection.domain.exception.SchemaValidationException;
import com.viewdatatools.apigenarator.collection.domain.exception.CollectionAlreadyExistsException;
import com.viewdatatools.apigenarator.collection.domain.exception.DocumentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleCollectionNotFound_shouldReturnNotFound() {
        CollectionNotFoundException exception = new CollectionNotFoundException("Collection not found");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleCollectionNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not Found", response.getBody().get("error"));
        assertEquals("Collection not found", response.getBody().get("message"));
    }

    @Test
    void handleCollectionAlreadyExists_shouldReturnConflict() {
        CollectionAlreadyExistsException exception = new CollectionAlreadyExistsException("Collection already exists");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleCollectionAlreadyExists(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Conflict", response.getBody().get("error"));
        assertEquals("Collection already exists", response.getBody().get("message"));
    }

    @Test
    void handleDocumentNotFound_shouldReturnNotFound() {
        DocumentNotFoundException exception = new DocumentNotFoundException("Document not found");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDocumentNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not Found", response.getBody().get("error"));
        assertEquals("Document not found", response.getBody().get("message"));
    }

    @Test
    void handleInvalidSchema_shouldReturnBadRequest() {
        InvalidSchemaException exception = new InvalidSchemaException("Invalid schema");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleInvalidSchema(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Invalid schema", response.getBody().get("message"));
    }

    @Test
    void handleSchemaValidation_shouldReturnBadRequest() {
        SchemaValidationException exception = new SchemaValidationException("Schema validation failed");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleSchemaValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Schema validation failed", response.getBody().get("message"));
    }

    @Test
    void handleIllegalArgument_shouldReturnBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgument(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Invalid argument", response.getBody().get("message"));
    }

    @Test
    void handleRuntime_shouldReturnBadRequest() {
        RuntimeException exception = new RuntimeException("Runtime error");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleRuntime(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().get("error"));
    }

    @Test
    void exceptionResponses_shouldContainTimestamp() {
        CollectionNotFoundException exception = new CollectionNotFoundException("Test");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleCollectionNotFound(exception);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("timestamp"));
    }

    @Test
    void exceptionResponses_shouldContainStatusCode() {
        InvalidSchemaException exception = new InvalidSchemaException("Test");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleInvalidSchema(exception);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("status"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void buildResponse_shouldIncludeAllFields() {
        InvalidSchemaException exception = new InvalidSchemaException("Test message");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleInvalidSchema(exception);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("timestamp"));
        assertTrue(response.getBody().containsKey("status"));
        assertTrue(response.getBody().containsKey("error"));
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Test message", response.getBody().get("message"));
    }
}
