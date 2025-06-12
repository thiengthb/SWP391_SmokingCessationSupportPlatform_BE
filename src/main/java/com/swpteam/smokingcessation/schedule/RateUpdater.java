package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.apis.currency.CurrencyRateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RateUpdater {

    CurrencyRateService currencyRateService;

    @Scheduled(fixedRate = 86400000)
    public void fetchRates() {
        currencyRateService.updateRates("USD");
    }
}

