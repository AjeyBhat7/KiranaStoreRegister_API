package com.jar.kiranaregister.AOP;

import io.github.bucket4j.Bucket;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

@Aspect
@Component
public class RateLimitAspect {

    private final ApplicationContext applicationContext;

    @Autowired
    public RateLimitAspect(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Around("@annotation(RateLimited)")
    public Object handleRateLimiting(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimited rateLimited = method.getAnnotation(RateLimited.class);

        String bucketName = rateLimited.bucketName();
        Bucket bucket;

        try {
            bucket = applicationContext.getBean(bucketName, Bucket.class); // Get the Bucket bean
        } catch (Exception e) {
            throw new IllegalStateException("Rate limiting bucket '" + bucketName + "' not found.", e);
        }

        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        } else {
            throw new RateLimitExceededException("Rate limit exceeded for " + bucketName);
        }
    }

    public static class RateLimitExceededException extends RuntimeException {
        public RateLimitExceededException(String message) {
            super(message);
        }
    }
}
