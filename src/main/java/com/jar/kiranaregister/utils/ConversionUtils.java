package com.jar.kiranaregister.utils;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConversionUtils {
    /**
     * Converts an amount from one currency to another based on the provided exchange rates.
     *
     * @param amount The amount to convert.
     * @param fromCurrency The original currency.
     * @param toCurrency The target currency.
     * @param exchangeRates A map of exchange rates.
     * @return The converted amount.
     */
    public static double getConvertedAmount(
            double amount,
            String fromCurrency,
            String toCurrency,
            Map<String, Double> exchangeRates) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        if (!exchangeRates.containsKey(fromCurrency) || !exchangeRates.containsKey(toCurrency)) {
            log.error("Exchange rate not found for: {} or {}", fromCurrency, toCurrency);
            throw new IllegalArgumentException(
                    "Exchange rate not found for: " + fromCurrency + " or " + toCurrency);
        }
        double usdAmount = amount / exchangeRates.get(fromCurrency); // Convert to USD
        return usdAmount * exchangeRates.get(toCurrency); // Convert to target currency
    }
}
