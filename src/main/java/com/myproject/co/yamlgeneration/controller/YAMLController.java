package com.myproject.co.yamlgeneration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;

import com.myproject.co.yamlgeneration.dto.YAMLRequest;
import com.myproject.co.yamlgeneration.service.YAMLService;

@RestController
public class YAMLController {

    @Autowired
    private YAMLService service;

    @PostMapping("/generate-yaml")
    public ResponseEntity<Map<String, Object>> generateYaml(@RequestBody YAMLRequest request) {
        try {
            Long id = service.saveAndGenerate1(request);
            return ResponseEntity.ok(Map.of("id", id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<FileSystemResource> downloadYaml(@PathVariable Long id) {
        File file = service.getFileForId(id);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=api.yaml")
                .contentType(MediaType.parseMediaType("text/yaml"))
                .body(new FileSystemResource(file));
    }
}
