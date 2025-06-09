package com.example.poll_system.infrastructure.persistence.jpa.cache;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisCacheStore<T> implements CacheStore<String, T> {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration TIME_TO_LIFE = Duration.ofHours(1);

    public RedisCacheStore(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<T> get(String key) {
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            return Optional.ofNullable((T) cached);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void put(String key, T value) {
        try {
            redisTemplate.opsForValue().set(key, value, TIME_TO_LIFE);
        } catch (Exception e) {
        }
    }

    @Override
    public void evict(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
        }
    }
}
