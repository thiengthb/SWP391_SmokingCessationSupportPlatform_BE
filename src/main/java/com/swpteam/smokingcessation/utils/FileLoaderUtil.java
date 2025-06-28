package com.swpteam.smokingcessation.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swpteam.smokingcessation.domain.dto.plan.PlanTemplateResponse;
import com.swpteam.smokingcessation.domain.dto.plan.PlanTemplateWrapper;
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

    public List<PlanTemplateResponse> loadPlanTemplate(String path) {
        try {
            InputStream inputStream = new ClassPathResource(path).getInputStream();

            PlanTemplateWrapper wrapper = objectMapper.readValue(inputStream, PlanTemplateWrapper.class);

            return wrapper.getLevels();
        }
        catch (IOException e) {
            log.error("Failed to load plan template from: {}", path, e);
            throw new RuntimeException("Unable to load plan template", e);
        }
    }

}
