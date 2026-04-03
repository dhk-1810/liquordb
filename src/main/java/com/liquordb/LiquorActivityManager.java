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
     * 주류 ID를 모든 기간의 활성 후보군(Active Set)에 추가
     */
    public void trackActivity(Long liquorId) {
        String idStr = String.valueOf(liquorId);

        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (PeriodType period : PeriodType.values()) {
                String key = ACTIVE_KEY_PREFIX + period.name();
                stringRedisTemplate.opsForSet().add(key, idStr);
            }
            return null;
        });
    }
}