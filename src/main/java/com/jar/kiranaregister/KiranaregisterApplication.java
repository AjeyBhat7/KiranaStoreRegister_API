package com.jar.kiranaregister;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class KiranaregisterApplication {

    public static void main(String[] args) {
        SpringApplication.run(KiranaregisterApplication.class, args);
    }
}
