package com.swpteam.smokingcessation.feature.integration.currency;

public interface ICurrencyRateService {

    void updateRates(String baseCurrency);

    Double getRate(String toCurrency);

    Double getNewPrice(double fromAmount, String fromCurrency, String toCurrency);
}
