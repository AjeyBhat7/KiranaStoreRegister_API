package com.jar.kiranaregister.utils;

import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.Interval;

public class ValidationUtils {

    /**
     * validates the currency is valid or not
     * @param currency
     * @return
     */
    public static CurrencyName validateCurrency(String currency) {
        try {
            return CurrencyName.valueOf(currency.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency: " + currency);
        }
    }

    /**
     * validate the passed interval is correct or not
     * @param interval
     * @return
     */
    public static Interval validateInterval(String interval) {

        try {
            return Interval.valueOf(interval.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid interval: " + interval);
        }
    }
}
