package com.liquordb.scheduler;

import com.liquordb.enums.PeriodType;
import com.liquordb.redis.RedisLockProvider;
import com.liquordb.service.LiquorRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 인기 주류 판별 및 캐싱 수행
 */
@RequiredArgsConstructor
@Component
public class PopularLiquorScheduler {

    private static final long LOCK_INTERVAL_SECONDS = 60L;

    private final LiquorRankingService rankingService;
    private final RedisLockProvider redisLockProvider;

    // 3시간 랭킹
    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    public void update3HourRanking() {
        redisLockProvider.executeIfLockAcquired("popular-liquor:3hours", LOCK_INTERVAL_SECONDS, () ->
                rankingService.calculateAndSaveRanking(PeriodType.THREE_HOURS)
        );
    }

    // 일간 랭킹
    @Scheduled(cron = "0 0 4 * * *")
    public void updateDailyRanking() {
        redisLockProvider.executeIfLockAcquired("popular-liquor:daily", LOCK_INTERVAL_SECONDS, () ->
                rankingService.calculateAndSaveRanking(PeriodType.DAILY)
        );
    }

    // 전체 기간 랭킹 (매일 새벽 4시 30분)
    @Scheduled(cron = "0 30 4 * * *")
    public void updateTotalRanking() {
        redisLockProvider.executeIfLockAcquired("popular-liquor:total", LOCK_INTERVAL_SECONDS, () ->
                rankingService.calculateAndSaveRanking(PeriodType.TOTAL)
        );
    }

    // 주간 랭킹
    @Scheduled(cron = "0 0 5 * * 1")
    public void updateWeeklyRanking() {
        redisLockProvider.executeIfLockAcquired("popular-liquor:weekly", LOCK_INTERVAL_SECONDS, () ->
                rankingService.calculateAndSaveRanking(PeriodType.WEEKLY)
        );
    }
}