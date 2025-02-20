package com.jar.kiranaregister.configaration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * template for fetching data from external api
     *
     * @return
     */
    @Bean
    public RestTemplate restTemplateBean() {
        return new RestTemplate();
    }
}
