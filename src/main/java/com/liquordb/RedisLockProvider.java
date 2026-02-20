package com.liquordb;

import com.liquordb.exception.redis.RedisLockAcquisitionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;

@RequiredArgsConstructor
@Component
@Slf4j
public class RedisLockProvider {

    private static final Duration LOCK_TIMEOUT = Duration.ofSeconds(10);
    private static final String LOCK_KEY_PREFIX = "lock:";

    private static final String RELEASE_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " + // 락 주인 확인
                    "return redis.call('del', KEYS[1]) " + // 삭제
                    "else return 0 end";

    private final RedisTemplate<String, Object> redisTemplate;

    private final ThreadLocal<String> lockValueHolder = new ThreadLocal<>(); // 스레드별 고유 값 관리

    public void acquireLock(String key) {
        String lockKey = LOCK_KEY_PREFIX + key;
        String lockValue = Thread.currentThread().getName() + "-" + System.currentTimeMillis();
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();

        // Redis에 해당 키가 없을 때만 값을 저장
        Boolean acquired = valueOps.setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT);

        if (Boolean.TRUE.equals(acquired)) {
            log.debug("분산 락 획득 성공: {} (값: {})", lockKey, lockValue);
        } else {
            log.debug("분산 락 획득 실패: {}", lockKey);
            throw new RedisLockAcquisitionException("분산 락 획득 실패: " + lockKey);
        }
    }

    public void releaseLock(String key) {
        String lockKey = LOCK_KEY_PREFIX + key;
        String originalValue = lockValueHolder.get();
        try {
            // Lua 스크립트 실행: 키와 내가 가졌던 값을 전달
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(RELEASE_SCRIPT, Long.class);
            Long result = redisTemplate.execute(script, Collections.singletonList(lockKey), originalValue);

            if (Long.valueOf(1).equals(result)) {
                log.debug("분산 락 해제 완료: {}", lockKey);
            } else {
                log.warn("분산 락 해제 실패. 이미 만료되었거나 다른 스레드의 락임. lockKey: {}", lockKey);
            }
        } finally {
            lockValueHolder.remove(); // 메모리 누수 방지
        }
    }

    // TODO Redisson 도입

}