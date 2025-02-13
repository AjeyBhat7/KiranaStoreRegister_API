package com.jar.kiranaregister.service.serviceImplementation;

import com.jar.kiranaregister.AOP.RateLimited;
import com.jar.kiranaregister.model.responseModel.FxRatesResponse;
import com.jar.kiranaregister.utils.StringUtils;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Service
public class FxRatesService {

    private final RestTemplate restTemplate;
    private final CacheService cacheService;
    
    @Autowired
    public FxRatesService(RestTemplate restTemplate, CacheService cacheService) {
        this.restTemplate = restTemplate;
        this.cacheService = cacheService;
       
    }

    @RateLimited(bucketName = "FxRatesRateLimitBucket")
    public FxRatesResponse getLatestFxRates() {
        final String url = "https://api.fxratesapi.com/latest";
       final String cacheKey = "FxRates";

        try {
            if (cacheService.checkKeyExists(cacheKey)) {
                String fxRatesResponseStr = cacheService.getValueFromRedis(cacheKey);
                return StringUtils.fromJson(fxRatesResponseStr, FxRatesResponse.class);
            }

            FxRatesResponse fxRatesResponse = restTemplate.getForObject(url, FxRatesResponse.class);

            if (fxRatesResponse != null) {
                String fxRatesResponseStr = StringUtils.toJson(fxRatesResponse);
                cacheService.setValueToRedis(cacheKey, fxRatesResponseStr, 300L);
            }

            return fxRatesResponse;
        } catch (RestClientException e) {
            return null;
        }
    }
}
