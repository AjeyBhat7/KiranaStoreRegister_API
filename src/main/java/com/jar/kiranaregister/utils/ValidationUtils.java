package com.jar.kiranaregister.utils;

import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.Interval;

public class ValidationUtils {

    public static CurrencyName validateCurrency(String currency) {
        try {
            return CurrencyName.valueOf(currency.toUpperCase());
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
