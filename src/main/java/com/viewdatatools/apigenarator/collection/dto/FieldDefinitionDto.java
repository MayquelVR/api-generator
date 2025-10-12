package com.viewdatatools.apigenarator.collection.dto;

import lombok.Data;

import java.util.Map;

@Data
public class FieldDefinitionDto {
    private String type;
    private Boolean required;
    private Integer maxLength;
    private Object defaultValue;
    private Map<String, FieldDefinitionDto> properties;
    private FieldDefinitionDto items;
}

