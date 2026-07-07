package com.liquordb.scheduler;

import com.liquordb.enums.PeriodType;
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

    private final LiquorRankingService rankingService;

    // 3시간 랭킹
    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    public void update3HourRanking() {
        rankingService.calculateAndSaveRanking(PeriodType.THREE_HOURS);
    }

    // 일간 랭킹
    @Scheduled(cron = "0 0 4 * * *")
    public void updateDailyRanking() {
        rankingService.calculateAndSaveRanking(PeriodType.DAILY);
    }

    // 전체 기간 랭킹 (매일 새벽 4시 30분)
    @Scheduled(cron = "0 30 4 * * *")
    public void updateTotalRanking() {
        rankingService.calculateAndSaveRanking(PeriodType.TOTAL);
    }

    // 주간 랭킹
    @Scheduled(cron = "0 0 5 * * 1")
    public void updateWeeklyRanking() {
        rankingService.calculateAndSaveRanking(PeriodType.WEEKLY);
    }
}