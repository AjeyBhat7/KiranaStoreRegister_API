package com.jar.kiranaregister.ratelimiting.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {
    /**
     * Rate limit bucket for FxRates service.
     */
    @Bean(name = "FxRatesRateLimitBucket")
    public Bucket FxRatesRateLimitBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofSeconds(1))))
                .build();
    }

    /**
     * Rate limit bucket for login  Api
     */
    @Bean(name = "LoginRateLimiter")
    public Bucket LonginRateLimitBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofSeconds(3))))
                .build();
    }

    /**
     * Rate limit bucket for report generation Api
     */
    @Bean(name = "ReportGenerationRateLimiter")
    public Bucket RecordGenarationRateLimitBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))))
                .build();
    }
}
