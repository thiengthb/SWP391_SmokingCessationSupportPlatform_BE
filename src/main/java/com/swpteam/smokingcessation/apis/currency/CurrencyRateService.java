package com.swpteam.smokingcessation.apis.currency;

import com.swpteam.smokingcessation.config.CurrencyApiConfig;
import com.swpteam.smokingcessation.constants.ErrorCode;
import com.swpteam.smokingcessation.exception.AppException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CurrencyRateService {

    RestTemplate restTemplate;
    CurrencyApiConfig currencyApiConfig;
    Map<String, Double> latestRates = new ConcurrentHashMap<>();

    public CurrencyRateService(CurrencyApiConfig config) {
        this.restTemplate = new RestTemplate();
        this.currencyApiConfig = config;
    }

    public void updateRates(String baseCurrency) {
        String url = currencyApiConfig.getApiEndpoint(baseCurrency);
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = response.getBody();
            Map<String, Object> rates = (Map<String, Object>) body.get("conversion_rates");
            latestRates.clear();

            for (Map.Entry<String, Object> entry : rates.entrySet()) {
                String currency = entry.getKey();
                Object value = entry.getValue();
                double rate;
                if (value instanceof Number) {
                    rate = ((Number) value).doubleValue();
                } else throw new AppException(ErrorCode.INVALID_CURRENCY);

                latestRates.put(currency, rate);
            }
        }
    }

    public Double getRate(String toCurrency) {
        Double rate = latestRates.get(toCurrency);
        if (rate == null)
            throw new AppException(ErrorCode.INVALID_CURRENCY);

        return rate;
    }

    public Double getNewPrice(double fromAmount, String fromCurrency, String toCurrency) {
        Double fromCurrencyRate = getRate(fromCurrency);
        Double toCurrencyRate = getRate(toCurrency);

        if (fromCurrencyRate == null || toCurrencyRate == null)
            throw new AppException(ErrorCode.INVALID_CURRENCY);

        return (fromAmount / fromCurrencyRate) * toCurrencyRate;
    }
}

