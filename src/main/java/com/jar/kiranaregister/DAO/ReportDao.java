package com.jar.kiranaregister.DAO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jar.kiranaregister.model.DTOModel.ReportDTO;
import com.jar.kiranaregister.service.serviceImplementation.CacheService;
import com.jar.kiranaregister.utils.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class ReportDao {

    private final CacheService cacheService;

    private static final long REPORT_CACHE_TTL = TimeUnit.DAYS.toSeconds(1); // 1-day cache TTL

    public ReportDao(CacheService cacheService) {
        this.cacheService = cacheService;

    }

    public void saveReport(String interval, String currency, ReportDTO report) {
        String key = generateKey(interval, currency);

        String jsonReport = StringUtils.toJson(report);
        cacheService.setValueToRedis(key, jsonReport, REPORT_CACHE_TTL);

    }

    public ReportDTO getReport(String interval, String currency) {
        String key = generateKey(interval, currency);
        if (!cacheService.checkKeyExists(key)) {
            return null;
        }

        String jsonReport = cacheService.getValueFromRedis(key);

            return StringUtils.fromJson(jsonReport, ReportDTO.class);

    }

    private String generateKey(String interval, String currency) {
        return "report:" + interval + ":" + currency;
    }
}
