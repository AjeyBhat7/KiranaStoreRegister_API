package com.jar.kiranaregister.AOP;

import io.github.bucket4j.Bucket;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Aspect
@Component
public class RateLimitAspect {

    private final Map<String, Bucket> buckets;

    @Autowired
    public RateLimitAspect(Map<String, Bucket> buckets) {
        this.buckets = buckets;
    }

    @Around("@annotation(rateLimited)")
    public Object handleRateLimiting(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String bucketQualifier = rateLimited.bucketQualifier();
        Bucket bucket = buckets.get(bucketQualifier);

        if (bucket == null) {
            throw new IllegalArgumentException("No bucket found for qualifier: " + bucketQualifier);
        }

        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        } else {
            throw new RuntimeException("Rate limit exceeded");
        }
    }
}
