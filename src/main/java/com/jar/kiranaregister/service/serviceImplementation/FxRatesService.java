package com.jar.kiranaregister.service.serviceImplementation;

import com.jar.kiranaregister.model.responseModel.FxRatesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FxRatesService {
    private final RestTemplate restTemplate;

    @Autowired
    public FxRatesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public FxRatesResponse getLatestFxRates() {
        String url = "https://api.fxratesapi.com/latest";

        return restTemplate.getForObject(url, FxRatesResponse.class);
    }
}
