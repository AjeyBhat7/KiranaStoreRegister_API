package com.jar.kiranaregister.feature_report.dao;

import static com.jar.kiranaregister.feature_report.utils.ReportUtils.generateKey;

import com.jar.kiranaregister.cache.service.CacheService;
import com.jar.kiranaregister.feature_report.model.responseObj.ReportResponse;
import com.jar.kiranaregister.utils.StringUtils;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDao {

    private final CacheService cacheService;

    private static final long REPORT_CACHE_TTL = TimeUnit.DAYS.toSeconds(1); // 1-day cache TTL

    public ReportDao(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * stores report in cache based on interval and currency.
     *
     * @param interval
     * @param currency
     * @param report
     */
    public void saveReport(String interval, String currency, ReportResponse report) {
        String key = generateKey(interval, currency);

        String jsonReport = StringUtils.toJson(report);
        cacheService.setValueToRedis(key, jsonReport, REPORT_CACHE_TTL);
    }

    /**
     * fetch report from cache based on interval and currency.
     *
     * @param interval
     * @param currency
     * @return
     */
    public ReportResponse getReport(String interval, String currency) {
        String key = generateKey(interval, currency);
        if (!cacheService.checkKeyExists(key)) {
            return null;
        }

        String jsonReport = cacheService.getValueFromRedis(key);

        return StringUtils.fromJson(jsonReport, ReportResponse.class);
    }
}
