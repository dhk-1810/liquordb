package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorScoreDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorRanking;
import com.liquordb.enums.PeriodType;
import com.liquordb.mapper.LiquorMapper;
import com.liquordb.repository.LiquorLikeRepository;
import com.liquordb.repository.liquor.LiquorRankingRepository;
import com.liquordb.repository.liquor.LiquorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LiquorRankingService {

    private final LiquorRepository liquorRepository;
    private final LiquorRankingRepository liquorRankingRepository;
    private final LiquorLikeRepository liquorLikeRepository;
    private final S3Service s3Service; // 단방향 참조

    private final RedisTemplate<String, String> redisTemplate;
    private static final String ACTIVE_KEY_PREFIX = "active:liquors:";
    private static final String RANKING_KEY_PREFIX = "ranking:";
    private static final int LIMIT = 10;

    public List<LiquorSummaryDto> getTrending(PeriodType period, UUID userId) {

        String rankingKey = RANKING_KEY_PREFIX + period;
        Set<String> topIds = redisTemplate.opsForZSet().reverseRange(rankingKey, 0, LIMIT - 1); // 점수 내림차순

        List<Long> ids;
        if (topIds != null && !topIds.isEmpty()) { // Redis에서 ID 조회
            ids = topIds.stream().map(Long::valueOf).toList();
        } else { // DB에서 ID 조회 (Fallback)
            ids = liquorRankingRepository.findTrendingLiquorIdsByPeriod(period);
        }

        if (!ids.isEmpty()) { // Fallback - 좋아요 많은 순
            Set<Long> likedLiquorIds = (userId != null)
                    ? liquorLikeRepository.findLikedLiquorIdsByUserIdAndLiquorIds(userId, ids)
                    : Collections.emptySet();

            Map<Long, Liquor> liquorMap = liquorRepository.findByIdIn(ids).stream()
                    .collect(Collectors.toMap(Liquor::getId, liquor -> liquor));

            return ids.stream()
                    .map(liquorMap::get)
                    .filter(Objects::nonNull)
                    .map(liquor -> {
                        boolean isLiked = likedLiquorIds.contains(liquor.getId());
                        String imageUrl = s3Service.getLiquorImageUrl(liquor.getImageKey()); // null-safe
                        return LiquorMapper.toSummaryDto(liquor, imageUrl, isLiked);
                    })
                    .toList();
        }

        List<Liquor> topLikedLiquors = liquorRepository.findTopLikedLiquors(PageRequest.of(0, 10));
        if (topLikedLiquors.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> topLikedIds = topLikedLiquors.stream().map(Liquor::getId).toList();
        Set<Long> likedLiquorIds = (userId != null)
                ? liquorLikeRepository.findLikedLiquorIdsByUserIdAndLiquorIds(userId, topLikedIds)
                : Collections.emptySet();

        return topLikedLiquors.stream()
                .map(liquor -> {
                    boolean isLiked = likedLiquorIds.contains(liquor.getId());
                    String imageUrl = s3Service.getLiquorImageUrl(liquor.getImageKey()); // null-safe
                    return LiquorMapper.toSummaryDto(liquor, imageUrl, isLiked);
                })
                .toList();
    }

    /**
     * 스케줄러에서만 호출
     */
    public void calculateAndSaveRanking(PeriodType period) {

        String activeKey = ACTIVE_KEY_PREFIX + period.name(); // 활동 발생 주류
        String rankingKey = RANKING_KEY_PREFIX + period.name(); // 랭킹 집계된 주류

        redisTemplate.delete(rankingKey); // 기존 랭킹 정보 삭제
        liquorRankingRepository.deleteByPeriodType(period);

        List<LiquorRanking> rankings = new ArrayList<>();

        if (period == PeriodType.TOTAL) {
            List<LiquorScoreDto> scores = liquorRepository.findScoresForTotalRanking(PageRequest.of(0, 10));
            for (int i = 0; i < scores.size(); i++) {
                LiquorScoreDto scoreDto = scores.get(i);
                long score = LiquorScoreDto.calculateTotalScore(scoreDto);
                int rank = i + 1;

                redisTemplate.opsForZSet().add(rankingKey, String.valueOf(scoreDto.liquorId()), score);
                rankings.add(LiquorRanking.create(period, scoreDto.liquorId(), score, rank));
            }
        } else {
            Set<ZSetOperations.TypedTuple<String>> topActive = redisTemplate.opsForZSet() // TypedTuple으로 값, 점수 쌍 가져옴
                    .reverseRangeWithScores(activeKey, 0, 9);

            if (topActive != null && !topActive.isEmpty()) {
                int rank = 1;
                for (ZSetOperations.TypedTuple<String> tuple : topActive) {
                    Long liquorId = Long.valueOf(tuple.getValue());
                    long score = Math.round(tuple.getScore() != null ? tuple.getScore() : 0.0);

                    redisTemplate.opsForZSet().add(rankingKey, String.valueOf(liquorId), score);
                    rankings.add(LiquorRanking.create(period, liquorId, score, rank));
                    rank++;
                }
            }
            redisTemplate.delete(activeKey); // 집계 완료 후 활동 발생 주류 초기화
        }

        if (!rankings.isEmpty()) {
            liquorRankingRepository.saveAll(rankings);
        }
    }
}
