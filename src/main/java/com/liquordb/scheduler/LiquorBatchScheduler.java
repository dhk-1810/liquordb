package com.liquordb.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class LiquorBatchScheduler {

    private final StringRedisTemplate redisTemplate;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final String LIKE_BATCH_KEY = "liquor:batch:likes";

    // 5분마다 실행 (cron 설정 또는 fixedDelay)
    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void writeBackLikesToDb() {

        // Redis에서 쌓인 데이터 가져오기
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(LIKE_BATCH_KEY);
        if (entries.isEmpty()) return;

        // 해당 키 삭제
        redisTemplate.delete(LIKE_BATCH_KEY);

        // 쿼리 파라미터 맵 배열 준비
        List<MapSqlParameterSource> batchArgs = entries.entrySet().stream()
                .map(entry -> new MapSqlParameterSource()
                        .addValue("id", Long.valueOf(entry.getKey().toString()))
                        .addValue("delta", Integer.valueOf(entry.getValue().toString())))
                .toList();

        // 벌크 업데이트 연산 수행
        String sql = "UPDATE liquors SET like_count = like_count + :delta WHERE id = :id";

        jdbcTemplate.batchUpdate(sql, batchArgs.toArray(new MapSqlParameterSource[0]));
    }

}
