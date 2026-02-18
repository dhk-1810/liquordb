package com.liquordb.security;

import com.liquordb.RedisLockProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * 리프레시 토큰, 로그아웃된 엑세스 토큰 저장
 * 동시 로그인 기기 대수 제한, 무효화된 엑세스 토큰 거부
 * 토큰 유효기간 만료 시 캐시에서 삭제
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class RedisJwtRegistry implements JwtRegistry {

    private final JwtProperties jwtProperties;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisLockProvider redisLockProvider;

    private static final String REFRESH_USER_LIST_PREFIX = "jwt:refresh:user";
    private static final String REFRESH_TOKEN_PREFIX = "jwt:refresh:token";
    private static final String REFRESH_LOCK_PREFIX = "lock:jwt:refresh:";
    private static final String BLACKLIST_TOKEN_PREFIX = "jwt:blacklist:index:";
    private static final String BLACKLIST_LOCK_KEY_PREFIX = "lock:jwt:blacklist:";
    private static final int MAX_ACTIVE_JWT_COUNT = 3; // 동시 접속 제한
    private final Duration REFRESH_TOKEN_VALIDITY = Duration.ofMillis(jwtProperties.getRefreshTokenValidityInMs());

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
            redisTemplate.expire(userKey, REFRESH_TOKEN_VALIDITY);

            redisTemplate.opsForValue().set(tokenIndexKey, userId.toString(), REFRESH_TOKEN_VALIDITY);

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
            redisTemplate.opsForValue().set(newTokenIndexKey, userId.toString(), REFRESH_TOKEN_VALIDITY);

            // 전체 리스트 만료 시간 갱신
            redisTemplate.expire(userKey, REFRESH_TOKEN_VALIDITY);

        } finally {
            redisLockProvider.releaseLock(lockKey);
        }
    }

    @Override
    public void invalidateAllRefreshTokensByUserId(UUID userId) {

        String userKey = getRefreshUserKey(userId);
        String lockKey = getRefreshLockKey(userId);

        redisLockProvider.acquireLock(lockKey);
        redisTemplate.delete(userKey);
    }

    @Override
    public boolean isRefreshTokenActive(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        return redisTemplate.hasKey(key); // 토큰 자체를 키로 사용하여 바로 조회
    }

    @Override
    public void addToBlacklist(String accessToken, long remainingTtlMs) {
        String key = getBlacklistIndexKey(accessToken);
        // 락 없이 바로 저장 (Atomic 연산)
        redisTemplate.opsForValue().set(key, "logout", Duration.ofMillis(remainingTtlMs));
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        String key = BLACKLIST_TOKEN_PREFIX + accessToken;
        return redisTemplate.hasKey(key);
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

    private String getBlacklistIndexKey(String token) {
        return BLACKLIST_TOKEN_PREFIX + token;
    }

    private String getBlacklistLockKey(String accessToken) {
        return BLACKLIST_LOCK_KEY_PREFIX + accessToken;
    }

    //    @Override
//    @CacheEvict(value = "users", key = "'all'")
//    @Retryable(retryFor = RedisLockAcquisitionException.class, maxAttempts = 3,
//            backoff = @Backoff(delay = 100, multiplier = 2))
//    public void registerJwtInformation(JwtInformation jwtInformation) {
//        String userKey = getUserKey(jwtInformation.userId());ㄹ
//        String lockKey = jwtInformation.userId().toString();
//
//        redisLockProvider.acquireLock(lockKey);
//        try {
//            Long currentSize = redisTemplate.opsForList().size(userKey);
//
//            while (currentSize != null && currentSize >= MAX_ACTIVE_JWT_COUNT) {
//                Object oldestTokenObj = redisTemplate.opsForList().leftPop(userKey);
//                if (oldestTokenObj instanceof JwtInformation oldestToken) {
//                    removeTokenIndex(oldestToken.accessToken(), oldestToken.refreshToken());
//                }
//                currentSize = redisTemplate.opsForList().size(userKey);
//            }
//
//            redisTemplate.opsForList().rightPush(userKey, jwtInformation);
//            redisTemplate.expire(userKey, DEFAULT_TTL);
//            addTokenIndex(jwtInformation.accessToken(), jwtInformation.refreshToken());
//
//        } finally {
//            redisLockProvider.releaseLock(lockKey);
//        }
//
////        eventPublisher.publishEvent(new UserLogInOutEvent(jwtInformation.userId(), true));
//    }
//
//    @Override
//    @CacheEvict(value = "users", key = "'all'")
//    public void invalidateJwtInformationByUserId(UUID userId) {
//        String userKey = getUserKey(userId);
//
//        List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);
//        if (tokens != null) {
//            tokens.forEach(tokenObj -> {
//                if (tokenObj instanceof JwtInformation jwtInfo) {
//                    removeTokenIndex(jwtInfo.accessToken(), jwtInfo.refreshToken());
//                }
//            });
//        }
//
//        redisTemplate.delete(userKey);
////        eventPublisher.publishEvent(new UserLogInOutEvent(userId, false));
//    }
//
//    @Override
//    @Retryable(retryFor = RedisLockAcquisitionException.class, maxAttempts = 10,
//            backoff = @Backoff(delay = 100, multiplier = 2))
//    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInfo) {
//        String userKey = getUserKey(newJwtInfo.userId());
//        String lockKey = newJwtInfo.userId().toString();
//
//        redisLockProvider.acquireLock(lockKey);
//
//        try {
//            // 사용자의 모든 토큰 조회
//            List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);
//
//            if (tokens != null) {
//                for (int i = 0; i < tokens.size(); i++) {
//                    if (tokens.get(i) instanceof JwtInformation oldJwtInfo &&
//                            oldJwtInfo.refreshToken().equals(refreshToken)) {
//
//                        removeTokenIndex(oldJwtInfo.accessToken(), oldJwtInfo.refreshToken());
//
//                        JwtInformation rotatedJwtInfo = new JwtInformation(
//                                oldJwtInfo.dto(),
//                                newJwtInfo.accessToken(),
//                                newJwtInfo.refreshToken()
//                        );
//
//                        redisTemplate.opsForList().set(userKey, i, rotatedJwtInfo);
//                        addTokenIndex(newJwtInfo.accessToken(), newJwtInfo.refreshToken());
//                        redisTemplate.expire(userKey, DEFAULT_TTL);
//                        log.debug("JWT 로테이션 완료: User={}, NewAccessToken={}", oldJwtInfo.userId(), rotatedJwtInfo.accessToken());
//                        break;
//                    }
//                }
//            }
//
//        } finally {
//            redisLockProvider.releaseLock(lockKey);
//        }
//    }
}
