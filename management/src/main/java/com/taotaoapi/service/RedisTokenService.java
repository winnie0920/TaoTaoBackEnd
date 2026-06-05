package com.taotaoapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final StringRedisTemplate redisTemplate;

    private static final String ACCESS_TOKEN_PREFIX = "access-token:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh-token:";

    // 存 token
    public void saveAccessToken(Integer userId, String token, long ttlSeconds) {
        redisTemplate.opsForValue()
                .set(ACCESS_TOKEN_PREFIX + userId, token, Duration.ofSeconds(ttlSeconds));
    }

    // 存 refresh token
    public void saveRefreshToken(Integer userId, String token, long ttlSeconds) {
        redisTemplate.opsForValue()
                .set(REFRESH_TOKEN_PREFIX + userId, token, Duration.ofSeconds(ttlSeconds));
    }
    // 取得 access Token
    public String getAccessToken(Integer userId) {
        return redisTemplate.opsForValue().get(ACCESS_TOKEN_PREFIX + userId);
    }

    // 取得 refresh token
    public String getRefreshToken(Integer userId) {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
    }

    // 刪除（logout / revoke）
    public void revokeUser(Integer userId) {
        redisTemplate.delete(ACCESS_TOKEN_PREFIX + userId);
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }

    // 只踢 一般 token
    public void revokeAccess(Integer userId) {
        redisTemplate.delete(ACCESS_TOKEN_PREFIX + userId);
    }

}
