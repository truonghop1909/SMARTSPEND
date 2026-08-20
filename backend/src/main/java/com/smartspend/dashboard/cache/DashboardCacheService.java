package com.smartspend.dashboard.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class DashboardCacheService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    DashboardCacheService.class
            );

    private static final String PREFIX =
            "dashboard:";

    private static final Duration TTL =
            Duration.ofMinutes(10);

    private final RedisTemplate<String, Object> redisTemplate;

    public DashboardCacheService(
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    public String buildKey(
            Long userId,
            int year,
            int month
    ) {
        return PREFIX
                + userId
                + ":"
                + year
                + ":"
                + month;
    }

    public Object get(
            Long userId,
            int year,
            int month
    ) {
        String key =
                buildKey(
                        userId,
                        year,
                        month
                );

        try {
            return redisTemplate
                    .opsForValue()
                    .get(key);

        } catch (DataAccessException exception) {
            log.warn(
                    "Unable to read dashboard cache. key={}",
                    key
            );

            return null;
        }
    }

    public void put(
            Long userId,
            int year,
            int month,
            Object value
    ) {
        String key =
                buildKey(
                        userId,
                        year,
                        month
                );

        try {
            redisTemplate
                    .opsForValue()
                    .set(
                            key,
                            value,
                            TTL
                    );

        } catch (DataAccessException exception) {
            log.warn(
                    "Unable to write dashboard cache. key={}",
                    key
            );
        }
    }

    public void evict(
            Long userId,
            int year,
            int month
    ) {
        String key =
                buildKey(
                        userId,
                        year,
                        month
                );

        try {
            redisTemplate.delete(key);

        } catch (DataAccessException exception) {
            log.warn(
                    "Unable to evict dashboard cache. key={}",
                    key
            );
        }
    }
}