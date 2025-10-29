package com.viewdatatools.apigenarator.collection.domain.service;

import com.viewdatatools.apigenarator.collection.domain.exception.InvalidSchemaException;
import com.viewdatatools.apigenarator.collection.domain.exception.SchemaValidationException;
import com.viewdatatools.apigenarator.collection.domain.model.FieldDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SchemaValidatorServiceTest {

    private SchemaValidatorService validatorService;

    @BeforeEach
    void setUp() {
        validatorService = new SchemaValidatorService();
    }

    // Schema Validation Tests

    @Test
    void validateSchema_withValidSimpleSchema_shouldNotThrowException() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("name", FieldDefinition.builder().type("STRING").required(true).build());
        schema.put("age", FieldDefinition.builder().type("INTEGER").required(false).build());

        assertDoesNotThrow(() -> validatorService.validateSchema(schema));
    }

    @Test
    void validateSchema_withNullSchema_shouldThrowException() {
        InvalidSchemaException exception = assertThrows(
            InvalidSchemaException.class,
            () -> validatorService.validateSchema(null)
        );

        assertEquals("Schema cannot be null or empty", exception.getMessage());
    }

    @Test
    void validateSchema_withEmptySchema_shouldThrowException() {
        Map<String, FieldDefinition> schema = new HashMap<>();

        InvalidSchemaException exception = assertThrows(
            InvalidSchemaException.class,
            () -> validatorService.validateSchema(schema)
        );

        assertEquals("Schema cannot be null or empty", exception.getMessage());
    }

    @Test
    void validateSchema_withInvalidType_shouldThrowException() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("field1", FieldDefinition.builder().type("INVALID_TYPE").build());

        InvalidSchemaException exception = assertThrows(
            InvalidSchemaException.class,
            () -> validatorService.validateSchema(schema)
        );

        assertTrue(exception.getMessage().contains("Invalid type 'INVALID_TYPE'"));
    }

    @Test
    void validateSchema_withObjectTypeWithoutProperties_shouldThrowException() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("address", FieldDefinition.builder().type("OBJECT").build());

        InvalidSchemaException exception = assertThrows(
            InvalidSchemaException.class,
            () -> validatorService.validateSchema(schema)
        );

        assertTrue(exception.getMessage().contains("OBJECT but has no properties"));
    }

    @Test
    void validateSchema_withValidNestedObject_shouldNotThrowException() {
        Map<String, FieldDefinition> addressProperties = new HashMap<>();
        addressProperties.put("street", FieldDefinition.builder().type("STRING").build());
        addressProperties.put("city", FieldDefinition.builder().type("STRING").build());

        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("address", FieldDefinition.builder()
            .type("OBJECT")
            .properties(addressProperties)
            .build());

        assertDoesNotThrow(() -> validatorService.validateSchema(schema));
    }

    @Test
    void validateSchema_withArrayTypeWithoutItems_shouldThrowException() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("tags", FieldDefinition.builder().type("ARRAY").build());

        InvalidSchemaException exception = assertThrows(
            InvalidSchemaException.class,
            () -> validatorService.validateSchema(schema)
        );

        assertTrue(exception.getMessage().contains("ARRAY but has no items definition"));
    }

    @Test
    void validateSchema_withValidArray_shouldNotThrowException() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("tags", FieldDefinition.builder()
            .type("ARRAY")
            .items(FieldDefinition.builder().type("STRING").build())
            .build());

        assertDoesNotThrow(() -> validatorService.validateSchema(schema));
    }

    @Test
    void validateSchema_withMaxLengthOnNonString_shouldThrowException() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("age", FieldDefinition.builder()
            .type("INTEGER")
            .maxLength(10)
            .build());

        InvalidSchemaException exception = assertThrows(
            InvalidSchemaException.class,
            () -> validatorService.validateSchema(schema)
        );

        assertTrue(exception.getMessage().contains("maxLength can only be specified for STRING"));
    }

    @Test
    void validateSchema_withAllValidTypes_shouldNotThrowException() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("name", FieldDefinition.builder().type("STRING").build());
        schema.put("age", FieldDefinition.builder().type("INTEGER").build());
        schema.put("price", FieldDefinition.builder().type("DECIMAL").build());
        schema.put("active", FieldDefinition.builder().type("BOOLEAN").build());
        schema.put("birthDate", FieldDefinition.builder().type("DATE").build());

        assertDoesNotThrow(() -> validatorService.validateSchema(schema));
    }

    // Document Validation Tests

    @Test
    void validateDocument_withNullDocument_shouldThrowException() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("name", FieldDefinition.builder().type("STRING").required(true).build());

        SchemaValidationException exception = assertThrows(
            SchemaValidationException.class,
            () -> validatorService.validateDocument(null, schema)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void validateDocument_withMissingRequiredField_shouldThrowException() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("name", FieldDefinition.builder().type("STRING").required(true).build());

        Map<String, Object> document = new HashMap<>();

        SchemaValidationException exception = assertThrows(
            SchemaValidationException.class,
            () -> validatorService.validateDocument(document, schema)
        );

        assertTrue(exception.getMessage().contains("Required field 'name' is missing"));
    }

    @Test
    void validateDocument_withAllRequiredFields_shouldNotThrowException() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("name", FieldDefinition.builder().type("STRING").required(true).build());
        schema.put("age", FieldDefinition.builder().type("INTEGER").required(true).build());

        Map<String, Object> document = new HashMap<>();
        document.put("name", "John Doe");
        document.put("age", 30);

        assertDoesNotThrow(() -> validatorService.validateDocument(document, schema));
    }

    @Test
    void validateDocument_withOptionalFieldMissing_shouldNotThrowException() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("name", FieldDefinition.builder().type("STRING").required(true).build());
        schema.put("age", FieldDefinition.builder().type("INTEGER").required(false).build());

        Map<String, Object> document = new HashMap<>();
        document.put("name", "John Doe");

        assertDoesNotThrow(() -> validatorService.validateDocument(document, schema));
    }

    @Test
    void validateDocument_withComplexNestedSchema_shouldValidateCorrectly() {
        // Build nested schema
        Map<String, FieldDefinition> addressProperties = new HashMap<>();
        addressProperties.put("street", FieldDefinition.builder().type("STRING").required(true).build());
        addressProperties.put("city", FieldDefinition.builder().type("STRING").required(true).build());

        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("name", FieldDefinition.builder().type("STRING").required(true).build());
        schema.put("address", FieldDefinition.builder()
            .type("OBJECT")
            .properties(addressProperties)
            .required(true)
            .build());

        // Valid document
        Map<String, Object> addressData = new HashMap<>();
        addressData.put("street", "123 Main St");
        addressData.put("city", "New York");

        Map<String, Object> document = new HashMap<>();
        document.put("name", "John Doe");
        document.put("address", addressData);

        assertDoesNotThrow(() -> validatorService.validateDocument(document, schema));
    }

    @Test
    void validateDocument_withDecimalValue_shouldAcceptBigDecimal() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("price", FieldDefinition.builder().type("DECIMAL").required(true).build());

        Map<String, Object> document = new HashMap<>();
        document.put("price", new BigDecimal("99.99"));

        assertDoesNotThrow(() -> validatorService.validateDocument(document, schema));
    }

    @Test
    void validateDocument_withBooleanValue_shouldValidateCorrectly() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("active", FieldDefinition.builder().type("BOOLEAN").required(true).build());

        Map<String, Object> document = new HashMap<>();
        document.put("active", true);

        assertDoesNotThrow(() -> validatorService.validateDocument(document, schema));
    }
}

