package com.liquordb;

import com.liquordb.enums.PeriodType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LiquorActivityManager {

    private final StringRedisTemplate stringRedisTemplate;
    private static final String ACTIVE_KEY_PREFIX = "active:liquors:";

    /**
     * 주류 ID의 활동 점수를 단위 기간의 활성 후보군 ZSet에 가산
     */
    public void trackActivity(Long liquorId, int deltaScore) {
        String idStr = String.valueOf(liquorId);

        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (PeriodType period : PeriodType.values()) {
                if (period == PeriodType.TOTAL) {
                    continue; // TOTAL은 DB에서 직접 전체 집계하므로 제외
                }
                String key = ACTIVE_KEY_PREFIX + period.name();
                stringRedisTemplate.opsForZSet().incrementScore(key, idStr, deltaScore);
            }
            return null;
        });
    }
}