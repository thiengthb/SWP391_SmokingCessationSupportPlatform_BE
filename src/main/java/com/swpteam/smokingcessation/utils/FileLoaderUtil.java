package com.swpteam.smokingcessation.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipInitDTO;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@UtilityClass
public class FileLoaderUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String loadFileAsString(String path) {
        try {
            InputStream inputStream = new ClassPathResource(path).getInputStream();

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to load file: " + path, e);
        }
    }

    public Map<String, String> loadJsonAsMap(String path) {
        try {
            String json = loadFileAsString(path);

            return objectMapper.readValue(json, new TypeReference<>() {});
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON file: " + path, e);
        }
    }

    public List<MembershipInitDTO> loadMemberships(String path) {
        try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load memberships", e);
        }
    }

}
