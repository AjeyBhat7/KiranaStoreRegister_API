package com.jar.kiranaregister.feature_fxrates.service;

import com.jar.kiranaregister.feature_fxrates.model.responseObj.FxRatesResponse;
import com.jar.kiranaregister.ratelimiting.AOP.RateLimited;

public interface FxRatesService {

    /**
     * check fxRate are available in cahe and return fxrates map
     * if map is not available in cache fetches from fxRated api using restTemplate.
     * @return
     */
    @RateLimited(bucketName = "FxRatesRateLimitBucket")
    FxRatesResponse getLatestFxRates();
}
