package com.jar.kiranaregister.service.serviceImplementation;


import com.jar.kiranaregister.AOP.RateLimited;
import com.jar.kiranaregister.model.responseModel.FxRatesResponse;
import com.jar.kiranaregister.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FxRatesService {

    private final RestTemplate restTemplate;
    private final CacheService cacheService;

    @Autowired
    public FxRatesService(RestTemplate restTemplate, CacheService cacheService) {
        this.restTemplate = restTemplate;
        this.cacheService = cacheService;
    }


    @RateLimited(bucketQualifier = "FxRatesRateLimitBucket")
    public FxRatesResponse getLatestFxRates() {
        String url = "https://api.fxratesapi.com/latest";
        String cacheKey = "FxRates";

        if (cacheService.checkKeyExists(cacheKey)) {
            String fxRatesResponseStr = cacheService.getValueFromRedis(cacheKey);

            return  StringUtils.fromJson(fxRatesResponseStr, FxRatesResponse.class);
        }

        FxRatesResponse fxRatesResponse = restTemplate.getForObject(url, FxRatesResponse.class);

        if (fxRatesResponse != null) {
            String fxRatesResponseStr = StringUtils.toJson(fxRatesResponse);
            cacheService.setValueToRedis(cacheKey, fxRatesResponseStr, 300L);
        }

        return fxRatesResponse;
    }


}
