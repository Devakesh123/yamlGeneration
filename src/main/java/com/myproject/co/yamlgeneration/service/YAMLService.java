package com.myproject.co.yamlgeneration.service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.co.yamlgeneration.dto.YAMLRequest;
import com.myproject.co.yamlgeneration.model.YAMLData;
import com.myproject.co.yamlgeneration.repository.YAMLDataRepository;

@Service
public class YAMLService {

    @Autowired
    private YAMLDataRepository repository;

    // updated code 1
    public Long saveAndGenerate1(YAMLRequest request) throws Exception {
        YAMLData entity = YAMLData.builder()
                .operationName(request.getOperationName())
                .url(request.getUrl())
                .method(request.getMethod().toLowerCase())
                .requestBodyJson(request.getRequestBodyJson())
                .responseBodyJson(request.getResponseBodyJson())
                .build();

        YAMLData saved = repository.save(entity);

        Map<String, Object> yamlMap = new LinkedHashMap<>();
        yamlMap.put("openapi", "3.0.0");

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("title", "Generated API");
        info.put("version", "1.0.0");
        yamlMap.put("info", info);

        Map<String, Object> paths = new LinkedHashMap<>();
        Map<String, Object> methodMap = new LinkedHashMap<>();

        // Parameters
        List<Map<String, Object>> parameters = new ArrayList<>();
        if (request.getUrl().contains("{")) {
            String pathVar = request.getUrl().replaceAll(".*\\{(.*?)\\}.*", "$1");
            Map<String, Object> param = new LinkedHashMap<>();
            param.put("name", pathVar);
            param.put("in", "path");
            param.put("required", true);
            param.put("schema", Map.of("type", "string"));
            parameters.add(param);
        }

        // Request Body
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("required", true);
        requestBody.put("content", Map.of(
                "application/json", Map.of(
                        "schema", jsonToSchema(request.getRequestBodyJson())
                )
        ));

        // Responses
        Map<String, Object> responses = Map.of(
                "200", Map.of(
                        "description", "Successful response",
                        "content", Map.of(
                                "application/json", Map.of(
                                        "schema", jsonToSchema(request.getResponseBodyJson())
                                )
                        )
                )
        );

        methodMap.put("summary", request.getOperationName());
        methodMap.put("parameters", parameters);
        if (!request.getMethod().equalsIgnoreCase("GET")) {
            methodMap.put("requestBody", requestBody);
        }
        methodMap.put("responses", responses);

        paths.put(request.getUrl(), Map.of(request.getMethod().toLowerCase(), methodMap));
        yamlMap.put("paths", paths);

        Yaml yaml = new Yaml();
        String output = yaml.dump(yamlMap);

        File file = getFileForId(saved.getId());
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(output);
        }

        return saved.getId();
    }
    
    private Map<String, Object> jsonToSchema(String jsonString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonMap = mapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
        Map<String, Object> properties = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            Map<String, Object> field = new LinkedHashMap<>();
            field.put("type", "string"); // simplify for now
            field.put("example", entry.getValue());
            properties.put(entry.getKey(), field);
        }

        return Map.of(
                "type", "object",
                "properties", properties
        );
    }

    // initial code
    public Long saveAndGenerate(YAMLRequest request) throws Exception {
        YAMLData entity = YAMLData.builder()
                .operationName(request.getOperationName())
                .url(request.getUrl())
                .method(request.getMethod())
                .requestBodyJson(request.getRequestBodyJson())
                .responseBodyJson(request.getResponseBodyJson())
                .build();

        YAMLData saved = repository.save(entity);

        Map<String, Object> yamlMap = new HashMap<>();
        yamlMap.put("operationName", request.getOperationName());
        yamlMap.put("url", request.getUrl());
        yamlMap.put("method", request.getMethod());
        yamlMap.put("requestBody", request.getRequestBodyJson());
        yamlMap.put("responseBody", request.getResponseBodyJson());

        Yaml yaml = new Yaml();
        String output = yaml.dump(yamlMap);

        File file = getFileForId(saved.getId());
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(output);
        }

        return saved.getId();
    }

    public File getFileForId(Long id) {
        return Path.of(System.getProperty("java.io.tmpdir"), "yaml_" + id + ".yaml").toFile();
    }
}