package com.jar.kiranaregister.utils;

import com.jar.kiranaregister.enums.Currency;

public class ValidationUtils {

    public static Currency validateCurrency(String currency) {
        try {
            return Currency.valueOf(currency.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency: " + currency);
        }
    }
}
