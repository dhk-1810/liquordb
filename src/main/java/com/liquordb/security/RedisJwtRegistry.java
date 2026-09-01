package com.liquordb.security;

import com.liquordb.redis.RedisLockProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * 리프레시 토큰 관리 (동시 로그인 기기 대수 제한, 세션 무효화)
 * 토큰 유효기간 만료 시 캐시에서 삭제
 */
@Component
@Slf4j
public class RedisJwtRegistry implements JwtRegistry {

    private static final String REFRESH_USER_LIST_PREFIX = "jwt:refresh:user";
    private static final String REFRESH_TOKEN_PREFIX = "jwt:refresh:token";
    private static final String REFRESH_LOCK_PREFIX = "lock:jwt:refresh:";
    private static final int MAX_ACTIVE_JWT_COUNT = 3; // 동시 접속 제한

    private final Duration refreshTokenValidity;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisLockProvider redisLockProvider;

    public RedisJwtRegistry(
            JwtProperties jwtProperties,
            RedisTemplate<String, Object> redisTemplate,
            RedisLockProvider redisLockProvider
    ) {
        this.redisTemplate = redisTemplate;
        this.redisLockProvider = redisLockProvider;
        this.refreshTokenValidity = Duration.ofMillis(jwtProperties.getRefreshTokenValidityInMs());
    }

    @Override
    public void registerRefreshToken(UUID userId, String refreshToken) {

        String userKey = getRefreshUserKey(userId); // 데이터 저장 주소
        String lockKey = getRefreshLockKey(userId); // 다른 스레드가 수정 못하게 Lock
        String tokenIndexKey = getRefreshTokenIndexKey(refreshToken); // 유효한지 검사 위해 역색인 데이터 추가 저장

        redisLockProvider.acquireLock(lockKey); // 사용자 단위
        try {
            Long currentSize = redisTemplate.opsForList().size(userKey);
            while (currentSize != null && currentSize >= MAX_ACTIVE_JWT_COUNT) {
                redisTemplate.opsForList().leftPop(userKey);
                currentSize--;
            }
            redisTemplate.opsForList().rightPush(userKey, refreshToken);
            redisTemplate.expire(userKey, refreshTokenValidity);

            redisTemplate.opsForValue().set(tokenIndexKey, userId.toString(), refreshTokenValidity);

        } finally {
            redisLockProvider.releaseLock(lockKey);
        }
    }

    @Override
    public void rotateRefreshToken(String oldRefreshToken, String newRefreshToken, UUID userId) {

        String userKey = getRefreshUserKey(userId);
        String lockKey = getRefreshLockKey(userId);
        String oldTokenIndexKey = getRefreshTokenIndexKey(oldRefreshToken);
        String newTokenIndexKey = getRefreshTokenIndexKey(newRefreshToken);

        redisLockProvider.acquireLock(lockKey);
        try {
            redisTemplate.delete(oldTokenIndexKey);
            redisTemplate.opsForList().remove(userKey, 1, oldRefreshToken);

            redisTemplate.opsForList().rightPush(userKey, newRefreshToken);
            redisTemplate.opsForValue().set(newTokenIndexKey, userId.toString(), refreshTokenValidity);

            // 전체 리스트 만료 시간 갱신
            redisTemplate.expire(userKey, refreshTokenValidity);

        } finally {
            redisLockProvider.releaseLock(lockKey);
        }
    }

    @Override
    public void invalidateAllRefreshTokensByUserId(UUID userId) {

        String userKey = getRefreshUserKey(userId);
        String lockKey = getRefreshLockKey(userId);

        redisLockProvider.acquireLock(lockKey);
        try {
            redisTemplate.delete(userKey);
        } finally {
            redisLockProvider.releaseLock(lockKey);
        }
    }

    @Override
    public boolean isRefreshTokenActive(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        return redisTemplate.hasKey(key); // 토큰 자체를 키로 사용하여 바로 조회
    }

    @Override
    public void clearExpiredTokens() {
        // TTL 지나면 Redis에서 자동으로 삭제됨.
    }

    /**
     * 헬퍼 메서드들
     */

    private String getRefreshUserKey(UUID userId) {
        return REFRESH_USER_LIST_PREFIX + userId.toString();
    }

    private String getRefreshLockKey(UUID userId) {
        return REFRESH_LOCK_PREFIX + userId.toString();
    }

    private String getRefreshTokenIndexKey(String token) {
        return REFRESH_TOKEN_PREFIX + token;
    }

}
