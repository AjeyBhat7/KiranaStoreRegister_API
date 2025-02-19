package com.jar.kiranaregister.feature_transaction.model.requestObj;

import lombok.Data;

import java.util.Map;

@Data
public class CurrencyConversionRequest {
    private String fromCurrency;
    private String toCurrency;
    private Map<String, Double> exchangeRates;
}
