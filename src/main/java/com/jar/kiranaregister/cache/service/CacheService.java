//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.jar.kiranaregister.cache.service;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final RedisTemplate<String, String> redisKVTemplate;

    public CacheService(RedisTemplate<String, String> redisKVTemplate) {
        this.redisKVTemplate = redisKVTemplate;
    }

    /**
     * sets the value in redis with expiry .
     *
     * @param key
     * @param value
     * @param ttl
     */
    public void setValueToRedis(String key, String value, long ttl) {
        this.redisKVTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
    }

    /**
     * set the values in redis without expiry;
     *
     * @param key
     * @param value
     */
    public void setValueToRedisWithoutTtl(String key, String value) {
        this.redisKVTemplate.opsForValue().set(key, value);
    }

    /**
     * fetch value for specific key crom cahe
     *
     * @param key
     * @return
     */
    public String getValueFromRedis(String key) {
        return (String) this.redisKVTemplate.opsForValue().get(key);
    }

    /**
     * check requested key exist or not
     *
     * @param key
     * @return
     */
    public boolean checkKeyExists(String key) {
        return Boolean.TRUE.equals(this.redisKVTemplate.hasKey(key));
    }
}
