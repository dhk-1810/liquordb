package com.liquordb;

import com.liquordb.service.LiquorRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PopularLiquorScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final LiquorRankingService rankingService;

    private static final String ACTIVE_KEY_PREFIX = "active:liquors:";
    private static final String RANKING_KEY_PREFIX = "ranking:";

    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    public void update3HourRanking() {
        processRanking("3h");
    }

    // 2. 일간 랭킹 (매일 새벽에 한 번)
    @Scheduled(cron = "0 0 4 * * *") // 매일 새벽 4시
    public void updateDailyRanking() {
        processRanking("daily");
    }

    // 3. 주간 랭킹 (매주 월요일 새벽에 한 번)
    @Scheduled(cron = "0 0 5 * * 1") // 매주 월요일 새벽 5시
    public void updateWeeklyRanking() {
        processRanking("weekly");
    }

    private void processRanking(String type) {
        String activeKey = ACTIVE_KEY_PREFIX + type;
        String rankingKey = RANKING_KEY_PREFIX + type;

        Set<String> activeIds = redisTemplate.opsForSet().members(activeKey);
        if (activeIds == null || activeIds.isEmpty()) return;

        Set<Long> idSet = activeIds.stream().map(Long::parseLong).collect(Collectors.toSet());

        // 기간 타입(3h, daily, weekly)에 따라 서비스에서 알아서 집계 및 ZSet 저장
        rankingService.calculateAndSaveRanking(idSet, rankingKey);

        // 해당 기간의 활동 목록만 초기화
        redisTemplate.delete(activeKey);
    }
}