package com.viewdatatools.apigenarator.collection.domain.service;

import com.viewdatatools.apigenarator.collection.domain.exception.InvalidSchemaException;
import com.viewdatatools.apigenarator.collection.domain.exception.SchemaValidationException;
import com.viewdatatools.apigenarator.collection.domain.model.FieldDefinition;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * Service to validate documents against collection schemas
 */
@Service
public class SchemaValidatorService {

    private static final List<String> VALID_TYPES = List.of(
            "STRING", "INTEGER", "DECIMAL", "BOOLEAN", "DATE", "OBJECT", "ARRAY"
    );

    /**
     * Validates that a schema is well-formed
     */
    public void validateSchema(Map<String, FieldDefinition> schema) {
        if (schema == null || schema.isEmpty()) {
            throw new InvalidSchemaException("Schema cannot be null or empty");
        }

        for (Map.Entry<String, FieldDefinition> entry : schema.entrySet()) {
            String fieldName = entry.getKey();
            FieldDefinition field = entry.getValue();

            validateFieldDefinition(fieldName, field);
        }
    }

    private void validateFieldDefinition(String fieldName, FieldDefinition field) {
        if (field == null) {
            throw new InvalidSchemaException("Field definition cannot be null for field: " + fieldName);
        }

        if (field.getType() == null || !VALID_TYPES.contains(field.getType().toUpperCase())) {
            throw new InvalidSchemaException(
                    "Invalid type '" + field.getType() + "' for field '" + fieldName + "'. Valid types: " + VALID_TYPES
            );
        }

        String type = field.getType().toUpperCase();

        // Validate OBJECT type
        if ("OBJECT".equals(type)) {
            if (field.getProperties() == null || field.getProperties().isEmpty()) {
                throw new InvalidSchemaException(
                        "Field '" + fieldName + "' is of type OBJECT but has no properties defined"
                );
            }
            // Recursively validate nested properties
            for (Map.Entry<String, FieldDefinition> prop : field.getProperties().entrySet()) {
                validateFieldDefinition(fieldName + "." + prop.getKey(), prop.getValue());
            }
        }

        // Validate ARRAY type
        if ("ARRAY".equals(type)) {
            if (field.getItems() == null) {
                throw new InvalidSchemaException(
                        "Field '" + fieldName + "' is of type ARRAY but has no items definition"
                );
            }
            validateFieldDefinition(fieldName + "[]", field.getItems());
        }

        // Validate maxLength only for STRING
        if (field.getMaxLength() != null && !"STRING".equals(type)) {
            throw new InvalidSchemaException(
                    "maxLength can only be specified for STRING type. Field: " + fieldName
            );
        }
    }

    /**
     * Validates that a document conforms to a schema
     */
    public void validateDocument(Map<String, Object> document, Map<String, FieldDefinition> schema) {
        if (document == null) {
            throw new SchemaValidationException("Document cannot be null");
        }

        // Check all required fields are present
        for (Map.Entry<String, FieldDefinition> entry : schema.entrySet()) {
            String fieldName = entry.getKey();
            FieldDefinition fieldDef = entry.getValue();

            if (Boolean.TRUE.equals(fieldDef.getRequired()) && !document.containsKey(fieldName)) {
                throw new SchemaValidationException("Required field '" + fieldName + "' is missing");
            }
        }

        // Validate each field in the document
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            if (!schema.containsKey(fieldName)) {
                throw new SchemaValidationException(
                        "Field '" + fieldName + "' is not defined in the schema"
                );
            }

            FieldDefinition fieldDef = schema.get(fieldName);
            validateFieldValue(fieldName, value, fieldDef);
        }
    }

    private void validateFieldValue(String fieldName, Object value, FieldDefinition fieldDef) {
        // Null values are allowed for non-required fields
        if (value == null) {
            if (Boolean.TRUE.equals(fieldDef.getRequired())) {
                throw new SchemaValidationException("Required field '" + fieldName + "' cannot be null");
            }
            return;
        }

        String type = fieldDef.getType().toUpperCase();

        switch (type) {
            case "STRING":
                validateString(fieldName, value, fieldDef);
                break;
            case "INTEGER":
                validateInteger(fieldName, value);
                break;
            case "DECIMAL":
                validateDecimal(fieldName, value);
                break;
            case "BOOLEAN":
                validateBoolean(fieldName, value);
                break;
            case "DATE":
                validateDate(fieldName, value);
                break;
            case "OBJECT":
                validateObject(fieldName, value, fieldDef);
                break;
            case "ARRAY":
                validateArray(fieldName, value, fieldDef);
                break;
            default:
                throw new SchemaValidationException("Unknown type: " + type);
        }
    }

    private void validateString(String fieldName, Object value, FieldDefinition fieldDef) {
        if (!(value instanceof String)) {
            throw new SchemaValidationException(
                    "Field '" + fieldName + "' must be a STRING but got: " + value.getClass().getSimpleName()
            );
        }

        String strValue = (String) value;
        if (fieldDef.getMaxLength() != null && strValue.length() > fieldDef.getMaxLength()) {
            throw new SchemaValidationException(
                    "Field '" + fieldName + "' exceeds max length of " + fieldDef.getMaxLength()
            );
        }
    }

    private void validateInteger(String fieldName, Object value) {
        if (!(value instanceof Integer) && !(value instanceof Long)) {
            throw new SchemaValidationException(
                    "Field '" + fieldName + "' must be an INTEGER but got: " + value.getClass().getSimpleName()
            );
        }
    }

    private void validateDecimal(String fieldName, Object value) {
        if (!(value instanceof Double) && !(value instanceof Float) && !(value instanceof BigDecimal)
                && !(value instanceof Integer) && !(value instanceof Long)) {
            throw new SchemaValidationException(
                    "Field '" + fieldName + "' must be a DECIMAL but got: " + value.getClass().getSimpleName()
            );
        }
    }

    private void validateBoolean(String fieldName, Object value) {
        if (!(value instanceof Boolean)) {
            throw new SchemaValidationException(
                    "Field '" + fieldName + "' must be a BOOLEAN but got: " + value.getClass().getSimpleName()
            );
        }
    }

    private void validateDate(String fieldName, Object value) {
        if (!(value instanceof String)) {
            throw new SchemaValidationException(
                    "Field '" + fieldName + "' must be a DATE string (ISO-8601 format)"
            );
        }

        try {
            LocalDate.parse((String) value);
        } catch (DateTimeParseException e) {
            throw new SchemaValidationException(
                    "Field '" + fieldName + "' is not a valid DATE. Expected ISO-8601 format (yyyy-MM-dd)"
            );
        }
    }

    @SuppressWarnings("unchecked")
    private void validateObject(String fieldName, Object value, FieldDefinition fieldDef) {
        if (!(value instanceof Map)) {
            throw new SchemaValidationException(
                    "Field '" + fieldName + "' must be an OBJECT but got: " + value.getClass().getSimpleName()
            );
        }

        Map<String, Object> objectValue = (Map<String, Object>) value;
        Map<String, FieldDefinition> properties = fieldDef.getProperties();

        if (properties == null) {
            return;
        }

        // Validate nested object
        for (Map.Entry<String, FieldDefinition> propEntry : properties.entrySet()) {
            String propName = propEntry.getKey();
            FieldDefinition propDef = propEntry.getValue();

            if (Boolean.TRUE.equals(propDef.getRequired()) && !objectValue.containsKey(propName)) {
                throw new SchemaValidationException(
                        "Required field '" + fieldName + "." + propName + "' is missing"
                );
            }

            if (objectValue.containsKey(propName)) {
                validateFieldValue(fieldName + "." + propName, objectValue.get(propName), propDef);
            }
        }

        // Check for unexpected fields in object
        for (String key : objectValue.keySet()) {
            if (!properties.containsKey(key)) {
                throw new SchemaValidationException(
                        "Field '" + fieldName + "." + key + "' is not defined in the schema"
                );
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void validateArray(String fieldName, Object value, FieldDefinition fieldDef) {
        if (!(value instanceof List)) {
            throw new SchemaValidationException(
                    "Field '" + fieldName + "' must be an ARRAY but got: " + value.getClass().getSimpleName()
            );
        }

        List<Object> arrayValue = (List<Object>) value;
        FieldDefinition itemsDef = fieldDef.getItems();

        if (itemsDef == null) {
            return;
        }

        // Validate each item in the array
        for (int i = 0; i < arrayValue.size(); i++) {
            validateFieldValue(fieldName + "[" + i + "]", arrayValue.get(i), itemsDef);
        }
    }
}

