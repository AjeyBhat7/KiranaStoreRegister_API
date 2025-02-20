package com.jar.kiranaregister.feature_report.utils;

public class ReportUtils {

    /**
     * generates key for redis storage based on time interval and currency
     *
     * @param interval
     * @param currency
     * @return
     */
    public static String generateKey(String interval, String currency) {
        return "report:" + interval + ":" + currency;
    }
}
