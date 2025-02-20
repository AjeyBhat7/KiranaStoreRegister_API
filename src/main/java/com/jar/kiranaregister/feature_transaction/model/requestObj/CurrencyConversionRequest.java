package com.jar.kiranaregister.feature_transaction.model.requestObj;

import java.util.Map;
import lombok.Data;

@Data
public class CurrencyConversionRequest {
    private String fromCurrency;
    private String toCurrency;
    private Map<String, Double> exchangeRates;
}
