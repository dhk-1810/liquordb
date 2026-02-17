package com.liquordb.security;

import com.liquordb.RedisLockProvider;
import com.liquordb.exception.redis.RedisLockAcquisitionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 리프레시 토큰 저장 및 관리
 */

/**
 * ① while 루프 최적화
 *
 * 현재 while 안에서 size()를 계속 호출하는데, Redis 호출 횟수가 많아집니다. 처음에 currentSize를 가져왔으니, 루프 안에서는 currentSize--를 하며 메모리 상에서 계산하고 마지막에 반영하는 것이 더 빠릅니다.
 * ② @CacheEvict 위치
 *
 * @CacheEvict가 메서드 시작 시점에 실행되는데, 만약 아래 로직에서 예외가 발생해 Retryable이 돌게 되면 캐시만 반복적으로 날아갈 수 있습니다. 성공적으로 수행된 후에 캐시를 날리는 것이 데이터 일관성에 더 유리할 수 있습니다.
 * ③ 예외 처리
 *
 * removeTokenIndex에서 oldestTokenObj가 JwtInformation 타입이 아닐 경우에 대한 처리가 누락되면 List에 쓰레기 값이 쌓일 수 있으니 로그를 남기는 등 방어 로직이 필요합니다.
 *
 * 다음에 무엇을 도와드릴까요?
 *
 *     Set 인덱스 대신 String 키(setex)를 사용해 TTL까지 자동 관리하는 방식으로 리팩토링하기
 *
 *     removeTokenIndex 로직을 하나로 합쳐서 실행하는 루아 스크립트 작성하기
 *
 *     MAX_ACTIVE_JWT_COUNT를 넘었을 때 가장 오래된 세션을 로그아웃 시키는 알림(Event) 처리 고도화하기
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class RedisJwtRegistry implements JwtRegistry {

    private static final String USER_JWT_KEY_PREFIX = "jwt:user:";
    private static final String ACCESS_TOKEN_INDEX_KEY = "jwt:access_tokens";
    private static final String REFRESH_TOKEN_INDEX_KEY = "jwt:refresh_tokens";
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);
    private static final int MAX_ACTIVE_JWT_COUNT = 3;

    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisLockProvider redisLockProvider;

    @Override
    @CacheEvict(value = "users", key = "'all'")
    @Retryable(retryFor = RedisLockAcquisitionException.class, maxAttempts = 10,
            backoff = @Backoff(delay = 100, multiplier = 2))
    public void registerJwtInformation(JwtInformation jwtInformation) {
        String userKey = getUserKey(jwtInformation.userId());
        String lockKey = jwtInformation.userId().toString();

        redisLockProvider.acquireLock(lockKey);
        try {
            Long currentSize = redisTemplate.opsForList().size(userKey);

            while (currentSize != null && currentSize >= MAX_ACTIVE_JWT_COUNT) {
                Object oldestTokenObj = redisTemplate.opsForList().leftPop(userKey);
                if (oldestTokenObj instanceof JwtInformation oldestToken) {
                    removeTokenIndex(oldestToken.accessToken(), oldestToken.refreshToken());
                }
                currentSize = redisTemplate.opsForList().size(userKey);
            }

            redisTemplate.opsForList().rightPush(userKey, jwtInformation);
            redisTemplate.expire(userKey, DEFAULT_TTL);
            addTokenIndex(jwtInformation.accessToken(), jwtInformation.refreshToken());

        } finally {
            redisLockProvider.releaseLock(lockKey);
        }

//        eventPublisher.publishEvent(new UserLogInOutEvent(jwtInformation.userId(), true));
    }

    @Override
    @CacheEvict(value = "users", key = "'all'")
    public void invalidateJwtInformationByUserId(UUID userId) {
        String userKey = getUserKey(userId);

        List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);
        if (tokens != null) {
            tokens.forEach(tokenObj -> {
                if (tokenObj instanceof JwtInformation jwtInfo) {
                    removeTokenIndex(jwtInfo.accessToken(), jwtInfo.refreshToken());
                }
            });
        }

        redisTemplate.delete(userKey);
//        eventPublisher.publishEvent(new UserLogInOutEvent(userId, false));
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        String userKey = getUserKey(userId);
        Long size = redisTemplate.opsForList().size(userKey);
        return size != null && size > 0;
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(ACCESS_TOKEN_INDEX_KEY, accessToken)
        );
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(REFRESH_TOKEN_INDEX_KEY, refreshToken)
        );
    }

    @Override
    @Retryable(retryFor = RedisLockAcquisitionException.class, maxAttempts = 10,
            backoff = @Backoff(delay = 100, multiplier = 2))
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInfo) {
        String userKey = getUserKey(newJwtInfo.userId());
        String lockKey = newJwtInfo.userId().toString();

        redisLockProvider.acquireLock(lockKey);

        try {
            // 사용자의 모든 토큰 조회
            List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);

            if (tokens != null) {
                for (int i = 0; i < tokens.size(); i++) {
                    if (tokens.get(i) instanceof JwtInformation oldJwtInfo &&
                            oldJwtInfo.refreshToken().equals(refreshToken)) {

                        removeTokenIndex(oldJwtInfo.accessToken(), oldJwtInfo.refreshToken());

                        JwtInformation rotatedJwtInfo = new JwtInformation(
                                oldJwtInfo.dto(),
                                newJwtInfo.accessToken(),
                                newJwtInfo.refreshToken()
                        );

                        redisTemplate.opsForList().set(userKey, i, rotatedJwtInfo);
                        addTokenIndex(newJwtInfo.accessToken(), newJwtInfo.refreshToken());
                        redisTemplate.expire(userKey, DEFAULT_TTL);
                        log.debug("JWT 로테이션 완료: User={}, NewAccessToken={}", oldJwtInfo.userId(), rotatedJwtInfo.accessToken());
                        break;
                    }
                }
            }

        } finally {
            redisLockProvider.releaseLock(lockKey);
        }
    }

    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void clearExpiredJwtInformation() {
        Set<String> userKeys = redisTemplate.keys(USER_JWT_KEY_PREFIX + "*");

        for (String userKey : userKeys) {
            List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);

            if (tokens != null) {
                boolean hasValidTokens = false;

                for (int i = tokens.size() - 1; i >= 0; i--) {
                    if (tokens.get(i) instanceof JwtInformation jwtInfo) {
                        boolean isExpired =
                                !jwtTokenProvider.validateAccessToken(jwtInfo.accessToken()) ||
                                        !jwtTokenProvider.validateRefreshToken(jwtInfo.refreshToken());

                        if (isExpired) {
                            redisTemplate.opsForList().set(userKey, i, "EXPIRED");
                            redisTemplate.opsForList().remove(userKey, 1, "EXPIRED");
                            removeTokenIndex(jwtInfo.accessToken(), jwtInfo.refreshToken());
                        } else {
                            hasValidTokens = true;
                        }
                    }
                }

                if (!hasValidTokens) {
                    redisTemplate.delete(userKey);
                }
            }
        }
    }

    /**
     * 헬퍼 메서드들
     */
    private String getUserKey(UUID userId) {
        return USER_JWT_KEY_PREFIX + userId.toString();
    }

    private void addTokenIndex(String accessToken, String refreshToken) {
        // Set에 토큰 추가 (add: 중복되면 무시됨)
        redisTemplate.opsForSet().add(ACCESS_TOKEN_INDEX_KEY, accessToken);
        redisTemplate.opsForSet().add(REFRESH_TOKEN_INDEX_KEY, refreshToken);

        // 인덱스 키에도 만료 시간 설정 (메모리 누수 방지)
        redisTemplate.expire(ACCESS_TOKEN_INDEX_KEY, DEFAULT_TTL);
        redisTemplate.expire(REFRESH_TOKEN_INDEX_KEY, DEFAULT_TTL);
    }

    private void removeTokenIndex(String accessToken, String refreshToken) {
        // Set에서 토큰 제거
        redisTemplate.opsForSet().remove(ACCESS_TOKEN_INDEX_KEY, accessToken);
        redisTemplate.opsForSet().remove(REFRESH_TOKEN_INDEX_KEY, refreshToken);
    }
}
