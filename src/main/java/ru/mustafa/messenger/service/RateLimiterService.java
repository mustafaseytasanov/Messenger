package ru.mustafa.messenger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    /**
     * Checks whether the user has exceeded the limit.
     * @param action : Action name (e.g., "create_message")
     * @param userId : User ID
     * @param capacity : Maximum number of requests
     * @param period : Time for which this limit is granted
     * @return true - if the request is ALLOWED, false - if the limit is EXCEEDED
     */
    public boolean isAllowed(String action, Long userId, int capacity, Duration period) {
        String key = "rl:" + action + ":" + userId;

        // Atomically incrementing a counter in Redis
        Long currentRequests = redisTemplate.opsForValue().increment(key);

        if (currentRequests != null && currentRequests == 1) {
            // If this is the first request, set the key lifetime
            redisTemplate.expire(key, period);
        }

        // If the counter has exceeded the limit, return false (request blocked)
        return currentRequests != null && currentRequests <= capacity;
    }
}
