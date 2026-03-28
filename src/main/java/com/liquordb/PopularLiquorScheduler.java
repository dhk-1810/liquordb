package com.liquordb;

import com.liquordb.enums.PeriodType;
import com.liquordb.service.LiquorRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 인기 주류 판별 및 캐싱 수행
 */
@RequiredArgsConstructor
@Component
public class PopularLiquorScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final LiquorRankingService rankingService;

    private static final String ACTIVE_KEY_PREFIX = "active:liquors:"; // 집계 대상 (후보군)
    private static final String RANKING_KEY_PREFIX = "ranking:"; // 최종 계산된 순위 결과

    // 3시간 랭킹
    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    public void update3HourRanking() {
        processRanking(PeriodType.THREE_HOURS);
    }

    // 일간 랭킹
    @Scheduled(cron = "0 0 4 * * *")
    public void updateDailyRanking() {
        processRanking(PeriodType.DAILY);
    }

    // 주간 랭킹
    @Scheduled(cron = "0 0 5 * * 1")
    public void updateWeeklyRanking() {
        processRanking(PeriodType.WEEKLY);
    }

    private void processRanking(PeriodType period) {
        String activeKey = ACTIVE_KEY_PREFIX + period.name();
        String rankingKey = RANKING_KEY_PREFIX + period.name();

        Set<String> activeIds = redisTemplate.opsForSet().members(activeKey);
        if (activeIds == null || activeIds.isEmpty()) return;

        Set<Long> idSet = activeIds.stream().map(Long::parseLong).collect(Collectors.toSet());
        rankingService.calculateAndSaveRanking(idSet, rankingKey);

        redisTemplate.delete(activeKey);
    }
}