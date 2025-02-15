package com.jar.kiranaregister.ratelimiting.AOP;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    String bucketName(); // Reference bucket by name
}
