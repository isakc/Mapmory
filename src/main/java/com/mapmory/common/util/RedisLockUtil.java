package com.mapmory.common.util;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisLockUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean acquireLock(String lockKey, long expireTime) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", expireTime, TimeUnit.MILLISECONDS);
        return success != null && success;
    }

    public void releaseLock(String lockKey) {
        redisTemplate.delete(lockKey);
    }
}