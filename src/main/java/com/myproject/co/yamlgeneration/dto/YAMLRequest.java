package com.myproject.co.yamlgeneration.dto;

import lombok.Data;

@Data
public class YAMLRequest {
    private String operationName;
    private String url;
    private String method;
    private String requestBodyJson;
    private String responseBodyJson;
}