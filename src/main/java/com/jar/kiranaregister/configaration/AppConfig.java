package com.jar.kiranaregister.configaration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {


    @Bean
    public RestTemplate restTemplateBean() {
        return new RestTemplate();
    }

//    @Bean
//    public RestTemplate fxRatesRestTemplate() {
//
////        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
////        requestFactory.setConnectTimeout(5000);
////        requestFactory.setConnectionRequestTimeout(5000);
//        RestTemplate restTemplate = new RestTemplate();
////        restTemplate.setObservationRegistry(this.observationRegistry);
//
//        return restTemplate;
//
//    }
}
