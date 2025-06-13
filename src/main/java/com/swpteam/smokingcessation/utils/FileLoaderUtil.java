package com.swpteam.smokingcessation.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileLoaderUtil {

    ObjectMapper objectMapper;

    public FileLoaderUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Load raw file content as String
    public String loadFileAsString(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            try (InputStream inputStream = resource.getInputStream()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load file: " + path, e);
        }
    }

    // Load JSON and convert to Map<String, String>
    public Map<String, String> loadJsonAsMap(String path) {
        try {
            String json = loadFileAsString(path);
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON file: " + path, e);
        }
    }
}
