package com.viewdatatools.apigenarator.collection.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDefinition {
    private String type;
    private Boolean required;
    private Integer maxLength;
    private Object defaultValue;
    private Map<String, FieldDefinition> properties;
    private FieldDefinition items;
}

