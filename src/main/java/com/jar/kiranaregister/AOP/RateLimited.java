package com.jar.kiranaregister.AOP;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    String bucketQualifier() default "defaultRateLimitBucket";
}
