package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorScoreDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.enums.PeriodType;
import com.liquordb.repository.liquor.LiquorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LiquorRankingService {

    private final LiquorRepository liquorRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String ACTIVE_KEY_PREFIX = "active:liquors:";
    private static final String RANKING_KEY_PREFIX = "ranking:";
    private static final int LIMIT = 10;

    public List<LiquorSummaryDto> getTrending(PeriodType period){

        String rankingKey = RANKING_KEY_PREFIX + period;
        Set<String> topIds = redisTemplate.opsForZSet().reverseRange(rankingKey, 0, LIMIT - 1);

        if (topIds != null && !topIds.isEmpty()) {
            List<Long> ids = topIds.stream().map(Long::valueOf).toList();
            return // TODO Redis에서 가져오도록 변경
        }

        List<Long> dbRankingIds = rankingRepository.findLiquorIdsByPeriod(period);
        if (!dbRankingIds.isEmpty()) {
            // DB에 있던 랭킹 정보를 Redis에 다시 채워주는 작업(Cache Warm-up)을 여기서 해줘도 좋습니다.
            return liquorRepository.findTrendingLiquorSummaries(dbRankingIds, LIMIT);
        }

        return null;
    }

    /**
     * 스케줄러에서만 호출
     */
    public void calculateAndSaveRanking(Set<Long> activeIds, String rankingKey) {

        Set<Long> ids = activeIds.stream().map(Long::valueOf).collect(Collectors.toSet());

        // 주류별 + 단위기간별로 리뷰/좋아요/댓글 개수를 조회
        List<LiquorScoreDto> scores = liquorRepository.findScoresByIds(ids);

        // 기존 랭킹 삭제
        redisTemplate.delete(rankingKey);

        // ZSet에 저장
        for (LiquorScoreDto score : scores) {
            double totalScore = (score.reviewCount() * 10) + (score.likeCount() * 5) + (score.commentCount() * 2);
            redisTemplate.opsForZSet().add(rankingKey, String.valueOf(score.liquorId()), totalScore);
        }

        // TODO SummaryDto 자체를 캐싱
    }
}
