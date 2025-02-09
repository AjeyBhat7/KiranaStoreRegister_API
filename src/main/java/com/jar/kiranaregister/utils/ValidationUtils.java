package com.jar.kiranaregister.utils;

import com.jar.kiranaregister.enums.Currency;
import com.jar.kiranaregister.enums.Interval;

public class ValidationUtils {

    public static Currency validateCurrency(String currency) {
        try {
            return Currency.valueOf(currency.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency: " + currency);
        }
    }

    public static Interval validateInterval(String interval) {

        try {
            return Interval.valueOf(interval.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid interval: " + interval);
        }
    }
}
