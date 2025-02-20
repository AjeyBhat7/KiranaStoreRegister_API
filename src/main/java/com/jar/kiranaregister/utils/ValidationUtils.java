package com.jar.kiranaregister.utils;

import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.Interval;

public class ValidationUtils {

    /** validates the currency is valid or not */
    public static CurrencyName validateCurrency(String currency) {
        try {
            return CurrencyName.valueOf(currency.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency: " + currency);
        }
    }

    /** validate the provided interval is correct or not */
    public static Interval validateInterval(String interval) {

        try {
            return Interval.valueOf(interval.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid interval: " + interval);
        }
    }
}
