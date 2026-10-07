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

    /**
     * 스케줄러 및 배치 전용 분산 락 실행기
     * waitTime = 0 (즉시 시도, 락 획득 실패 시 대기 없이 건너뜀)
     * minIntervalSeconds 동안 락을 유지하여 다른 인스턴스의 중복 실행을 방지
     */
    public boolean executeIfLockAcquired(String key, long minIntervalSeconds, Runnable task) {
        String lockKey = LOCK_KEY_PREFIX + key;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean acquired = lock.tryLock(0, minIntervalSeconds, TimeUnit.SECONDS);
            if (acquired) {
                log.info("스케줄러 분산 락 획득 성공: {}", lockKey);
                try {
                    task.run();
                    return true;
                } catch (Exception e) {
                    log.error("스케줄러 작업 실행 중 예외 발생: {}", lockKey, e);
                    throw e;
                }
            } else {
                log.info("다른 인스턴스에서 이미 실행 중이거나 최근 실행되어 건너뜁니다: {}", lockKey);
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("스케줄러 분산 락 획득 인터럽트: {}", lockKey);
            return false;
        }
    }
}