//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.jar.kiranaregister.cache.service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final RedisTemplate<String, String> redisKVTemplate;

    public CacheService(RedisTemplate<String, String> redisKVTemplate) {
        this.redisKVTemplate = redisKVTemplate;
    }

    public void setValueToRedis(String key, String value, long ttl) {
        this.redisKVTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
    }

    public void setValueToRedisWithoutTtl(String key, String value) {
        this.redisKVTemplate.opsForValue().set(key, value);
    }

    public String getValueFromRedis(String key) {
        return (String) this.redisKVTemplate.opsForValue().get(key);
    }

    public boolean checkKeyExists(String key) {
        return Boolean.TRUE.equals(this.redisKVTemplate.hasKey(key));
    }

    public void unlinkKeys(List<String> keys) {
        this.redisKVTemplate.unlink(keys);
    }

    public void incrementKey(String key) {
        this.redisKVTemplate.opsForValue().increment(key);
    }

    public void decrementKey(String key) {
        this.redisKVTemplate.opsForValue().decrement(key);
    }
}
