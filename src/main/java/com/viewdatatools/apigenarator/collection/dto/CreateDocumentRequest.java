package com.viewdatatools.apigenarator.collection.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CreateDocumentRequest {
    private Map<String, Object> data;
}

