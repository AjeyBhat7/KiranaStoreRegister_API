package com.jar.kiranaregister.utils;

import com.jar.kiranaregister.feature_transaction.model.entity.Transaction;
import com.jar.kiranaregister.feature_transaction.model.requestObj.CurrencyConversionRequest;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CurrencyConversionUtils {
    /**
     * Converts an amount from one currency to another based on the provided exchange rates.
     *
     * @param request
     * @return
     */
    public static double getConvertedAmount(double amount, CurrencyConversionRequest request) {
        if (request.getFromCurrency().equals(request.getToCurrency())) {
            return amount;
        }

        Map<String, Double> exchangeRates = request.getExchangeRates();

        if (!exchangeRates.containsKey(request.getFromCurrency())
                || !exchangeRates.containsKey(request.getToCurrency())) {
            log.error(
                    "Exchange rate not found for: {} or {}",
                    request.getFromCurrency(),
                    request.getToCurrency());
            throw new IllegalArgumentException(
                    "Exchange rate not found for: "
                            + request.getFromCurrency()
                            + " or "
                            + request.getToCurrency());
        }

        double usdAmount = amount / exchangeRates.get(request.getFromCurrency()); // Convert to USD
        return usdAmount * exchangeRates.get(request.getToCurrency()); // Convert to target currency
    }

    /** generates new currency conversion request */
    public static CurrencyConversionRequest getCurrencyConversionRequest(
            String targetCurrency, Transaction transaction, Map<String, Double> exchangeRates) {
        CurrencyConversionRequest ccRequest = new CurrencyConversionRequest();
        ccRequest.setExchangeRates(exchangeRates);
        ccRequest.setFromCurrency(transaction.getCurrencyName().name());
        ccRequest.setToCurrency(targetCurrency);
        return ccRequest;
    }
}
