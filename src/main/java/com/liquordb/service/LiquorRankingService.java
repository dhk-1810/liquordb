package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorScoreDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.repository.liquor.LiquorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class LiquorRankingService {

    private final LiquorRepository liquorRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public List<LiquorSummaryDto> getTopRankings(int limit){
        return liquorRepository.findTrendingLiquorSummaries();
    }

    public void calculateAndSaveRanking(Set<Long> activeIds, String rankingKey) {

        List<Long> ids = activeIds.stream().map(Long::valueOf).toList();

        // Querydsl 등을 사용해 ID별로 리뷰/좋아요/댓글 개수를 가져옴
        List<LiquorScoreDto> scores = liquorRepository.findScoresByIds(ids);

        // Redis Sorted Set(ZSet)에 저장
        redisTemplate.delete(rankingKey); // 기존 랭킹 삭제

        for (LiquorScoreDto score : scores) {
            double totalScore = (score.reviewCount() * 10) + (score.likeCount() * 5) + (score.commentCount() * 2);
            redisTemplate.opsForZSet().add(rankingKey, String.valueOf(score.liquorId()), totalScore);
        }
    }
}
