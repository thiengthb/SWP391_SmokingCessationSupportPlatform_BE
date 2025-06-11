package com.swpteam.smokingcessation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyApiConfig {
    @Value("${currency.api.url}")
    private String apiUrl;

    @Value("${currency.api.key}")
    private String apiKey;

    public String getApiEndpoint(String baseCurrency) {
        return String.format("%s/%s/latest/%s", apiUrl, apiKey, baseCurrency);
    }
}

