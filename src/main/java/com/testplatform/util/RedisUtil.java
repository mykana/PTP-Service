package com.testplatform.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String TOKEN_KEY_PREFIX = "token:";
    private static final String USER_KEY_PREFIX = "user:";
    
    /**
     * 存储token
     */
    public void saveToken(String username, String token, long expiration) {
        String key = TOKEN_KEY_PREFIX + username;
        redisTemplate.opsForValue().set(key, token, expiration, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 获取token
     */
    public String getToken(String username) {
        String key = TOKEN_KEY_PREFIX + username;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * 删除token
     */
    public void deleteToken(String username) {
        String key = TOKEN_KEY_PREFIX + username;
        redisTemplate.delete(key);
    }
    
    /**
     * 存储用户信息
     */
    public void saveUser(String username, Object userInfo, long expiration) {
        String key = USER_KEY_PREFIX + username;
        redisTemplate.opsForValue().set(key, userInfo, expiration, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 获取用户信息
     */
    public Object getUser(String username) {
        String key = USER_KEY_PREFIX + username;
        return redisTemplate.opsForValue().get(key);
    }
    
    /**
     * 删除用户信息
     */
    public void deleteUser(String username) {
        String key = USER_KEY_PREFIX + username;
        redisTemplate.delete(key);
    }
} 