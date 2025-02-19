package com.jar.kiranaregister.feature_fxrates.service.serviceImplementation;

import com.jar.kiranaregister.cache.service.CacheService;
import com.jar.kiranaregister.feature_fxrates.model.responseObj.FxRatesResponse;
import com.jar.kiranaregister.feature_fxrates.service.FxRatesService;
import com.jar.kiranaregister.ratelimiting.AOP.RateLimited;
import com.jar.kiranaregister.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class FxRatesServiceImplementation implements FxRatesService {

    private final RestTemplate restTemplate;
    private final CacheService cacheService;

    @Autowired
    public FxRatesServiceImplementation(RestTemplate restTemplate, CacheService cacheService) {
        this.restTemplate = restTemplate;
        this.cacheService = cacheService;
    }

    /**
     * Fetches the latest foreign exchange rates. Uses caching to reduce API calls and rate limiting
     * for protection.
     *
     * @return FxRates response
     */
    @RateLimited(bucketName = "FxRatesRateLimitBucket")
    @Override
    public FxRatesResponse getLatestFxRates() {
        final String url = "https://api.fxratesapi.com/latest";
        final String cacheKey = "FxRates";

        try {
            // Check if data exists in cache
            if (cacheService.checkKeyExists(cacheKey)) {
                log.info("Fetching FX rates from cache.");
                String fxRatesResponseStr = cacheService.getValueFromRedis(cacheKey);
                return StringUtils.fromJson(fxRatesResponseStr, FxRatesResponse.class);
            }

            // Fetch FX rates from external API
            log.info("Fetching FX rates from external API.");
            FxRatesResponse fxRatesResponse = restTemplate.getForObject(url, FxRatesResponse.class);

            // Store response in cache if valid
            if (fxRatesResponse != null) {
                log.info("Storing FX rates in cache.");
                String fxRatesResponseStr = StringUtils.toJson(fxRatesResponse);
                cacheService.setValueToRedis(
                        cacheKey, fxRatesResponseStr, 300L); // Cache for 5 minutes
            }

            return fxRatesResponse;
        } catch (RestClientException e) {
            log.error("Failed to fetch FX rates: {}", e.getMessage());
            return null;
        }
    }
}
