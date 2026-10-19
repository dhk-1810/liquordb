package com.liquordb.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 리프레시 토큰 관리 (동시 로그인 기기 대수 제한, 세션 무효화)
 * Redis ZSet + Lua Script 기반으로 원자적 동시성 제어 및 토큰 만료 관리
 */
@Component
@Slf4j
public class RedisJwtRegistry implements JwtRegistry {

    private static final String REFRESH_USER_ZSET_PREFIX = "jwt:refresh:user:";
    private static final int MAX_ACTIVE_JWT_COUNT = 3; // 동시 접속 제한

    private static final RedisScript<Long> REGISTER_SCRIPT = new DefaultRedisScript<>(
            """
            local userKey = KEYS[1]
            local expireScore = tonumber(ARGV[1])
            local refreshToken = ARGV[2]
            local maxCount = tonumber(ARGV[3])
            local now = tonumber(ARGV[4])
            local ttlSeconds = tonumber(ARGV[5])

            redis.call('ZREMRANGEBYSCORE', userKey, '-inf', now)
            redis.call('ZADD', userKey, expireScore, refreshToken)
            redis.call('ZREMRANGEBYRANK', userKey, 0, -(maxCount + 1))
            redis.call('EXPIRE', userKey, ttlSeconds)

            return 1
            """,
            Long.class
    );

    private static final RedisScript<Long> ROTATE_SCRIPT = new DefaultRedisScript<>(
            """
            local userKey = KEYS[1]
            local oldToken = ARGV[1]
            local newToken = ARGV[2]
            local expireScore = tonumber(ARGV[3])
            local maxCount = tonumber(ARGV[4])
            local now = tonumber(ARGV[5])
            local ttlSeconds = tonumber(ARGV[6])

            redis.call('ZREM', userKey, oldToken)
            redis.call('ZREMRANGEBYSCORE', userKey, '-inf', now)
            redis.call('ZADD', userKey, expireScore, newToken)
            redis.call('ZREMRANGEBYRANK', userKey, 0, -(maxCount + 1))
            redis.call('EXPIRE', userKey, ttlSeconds)

            return 1
            """,
            Long.class
    );

    private final Duration refreshTokenValidity;
    private final StringRedisTemplate stringRedisTemplate;

    public RedisJwtRegistry(
            JwtProperties jwtProperties,
            StringRedisTemplate stringRedisTemplate
    ) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.refreshTokenValidity = Duration.ofMillis(jwtProperties.getRefreshTokenValidityInMs());
    }

    @Override
    public void registerRefreshToken(UUID userId, String refreshToken) {
        String userKey = getRefreshUserKey(userId);
        long now = System.currentTimeMillis();
        long expireTimestamp = now + refreshTokenValidity.toMillis();
        long ttlSeconds = refreshTokenValidity.toSeconds();

        List<String> keys = Collections.singletonList(userKey);
        stringRedisTemplate.execute(
                REGISTER_SCRIPT,
                keys,
                String.valueOf(expireTimestamp),
                refreshToken,
                String.valueOf(MAX_ACTIVE_JWT_COUNT),
                String.valueOf(now),
                String.valueOf(ttlSeconds)
        );
    }

    @Override
    public void rotateRefreshToken(String oldRefreshToken, String newRefreshToken, UUID userId) {
        String userKey = getRefreshUserKey(userId);
        long now = System.currentTimeMillis();
        long expireTimestamp = now + refreshTokenValidity.toMillis();
        long ttlSeconds = refreshTokenValidity.toSeconds();

        List<String> keys = Collections.singletonList(userKey);
        stringRedisTemplate.execute(
                ROTATE_SCRIPT,
                keys,
                oldRefreshToken,
                newRefreshToken,
                String.valueOf(expireTimestamp),
                String.valueOf(MAX_ACTIVE_JWT_COUNT),
                String.valueOf(now),
                String.valueOf(ttlSeconds)
        );
    }

    @Override
    public void invalidateAllRefreshTokensByUserId(UUID userId) {
        if (userId == null) {
            return;
        }
        String userKey = getRefreshUserKey(userId);
        stringRedisTemplate.delete(userKey);
    }

    @Override
    public boolean isRefreshTokenActive(UUID userId, String refreshToken) {
        if (userId == null || refreshToken == null) {
            return false;
        }
        String userKey = getRefreshUserKey(userId);
        Double score = stringRedisTemplate.opsForZSet().score(userKey, refreshToken);
        if (score == null) {
            return false;
        }
        return score > System.currentTimeMillis();
    }

    @Override
    public void clearExpiredTokens() {
        // ZSet 등록 및 로테이션 시점의 Lua 스크립트(ZREMRANGEBYSCORE)와 Redis Key TTL을 통해 자동 만료 및 정리됨.
    }

    private String getRefreshUserKey(UUID userId) {
        return REFRESH_USER_ZSET_PREFIX + userId.toString();
    }
}
