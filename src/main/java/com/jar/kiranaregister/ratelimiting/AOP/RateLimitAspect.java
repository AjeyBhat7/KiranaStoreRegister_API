package com.jar.kiranaregister.ratelimiting.AOP;

import com.jar.kiranaregister.exception.RateLimitExceededException;
import io.github.bucket4j.Bucket;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect {

    private final ApplicationContext applicationContext;

    @Autowired
    public RateLimitAspect(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * fecth the beans of buckets and checks tokens are available or not if tokens are not available
     * throws RateLimitExceededException
     *
     * @param joinPoint
     * @param rateLimited
     * @return
     * @throws Throwable
     */
    @Around("@annotation(rateLimited)")
    public Object handleRateLimiting(ProceedingJoinPoint joinPoint, RateLimited rateLimited)
            throws Throwable {

        String bucketName = rateLimited.bucketName();
        Bucket bucket;

        try {
            bucket = applicationContext.getBean(bucketName, Bucket.class); // Get the Bucket bean
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Rate limiting bucket '" + bucketName + "' not found.", e);
        }

        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        } else {
            throw new RateLimitExceededException("too many requests");
        }
    }
}
