package com.liquordb.redis;

import com.liquordb.exception.redis.RedisLockAcquisitionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Component
@Slf4j
public class RedisLockProvider {

    private static final String LOCK_KEY_PREFIX = "lock:";
    private static final long DEFAULT_WAIT_TIME_SECONDS = 5L;

    private final RedissonClient redissonClient;

    public void acquireLock(String key) {
        acquireLock(key, DEFAULT_WAIT_TIME_SECONDS, TimeUnit.SECONDS);
    }

    public void acquireLock(String key, long waitTime, TimeUnit timeUnit) {
        String lockKey = LOCK_KEY_PREFIX + key;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // leaseTime을 지정하지 않아 Redisson Watchdog 자동 연장 기능 활성화
            boolean acquired = lock.tryLock(waitTime, timeUnit);
            if (acquired) {
                log.debug("분산 락 획득 성공: {}", lockKey);
            } else {
                log.debug("분산 락 획득 타임아웃/실패: {}", lockKey);
                throw new RedisLockAcquisitionException("분산 락 획득 실패: " + lockKey);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RedisLockAcquisitionException("분산 락 획득 중 인터럽트 발생: " + lockKey);
        }
    }

    public void releaseLock(String key) {
        String lockKey = LOCK_KEY_PREFIX + key;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("분산 락 해제 완료: {}", lockKey);
            } else {
                log.warn("분산 락 해제 실패. 현재 스레드가 락을 보유하고 있지 않음. lockKey: {}", lockKey);
            }
        } catch (IllegalMonitorStateException e) {
            log.warn("분산 락 해제 예외 발생. lockKey: {}, message: {}", lockKey, e.getMessage());
        }
    }

    public void executeWithLock(String key, Runnable task) {
        acquireLock(key);
        try {
            task.run();
        } finally {
            releaseLock(key);
        }
    }

    public <T> T executeWithLock(String key, Supplier<T> task) {
        acquireLock(key);
        try {
            return task.get();
        } finally {
            releaseLock(key);
        }
    }
}