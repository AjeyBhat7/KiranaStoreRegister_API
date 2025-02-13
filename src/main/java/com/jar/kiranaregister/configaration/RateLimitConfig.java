package com.jar.kiranaregister.configaration;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Bean
    public Bucket defaultRateLimitBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(1, Refill.greedy(1, Duration.ofSeconds(1))))
                .build();
    }

    @Bean(name = "FxRatesRateLimitBucket")
    public Bucket FxRatesRateLimitBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofSeconds(1))))
                .build();
    }

    @Bean(name = "LoginRateLimiter")
    public Bucket LonginRateLimitBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofSeconds(3))))
                .build();
    }

    @Bean(name = "RecordGenerationRateLimiter")
    public Bucket RecordGenarationRateLimitBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))))
                .build();
    }
}
