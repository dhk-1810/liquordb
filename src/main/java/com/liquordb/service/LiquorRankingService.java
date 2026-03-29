package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorScoreDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.enums.PeriodType;
import com.liquordb.mapper.LiquorMapper;
import com.liquordb.repository.LiquorLikeRepository;
import com.liquordb.repository.liquor.LiquorRankingRepository;
import com.liquordb.repository.liquor.LiquorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LiquorRankingService {

    private final LiquorRepository liquorRepository;
    private final LiquorRankingRepository liquorRankingRepository;
    private final LiquorLikeRepository liquorLikeRepository;
    private final S3Service s3Service;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String ACTIVE_KEY_PREFIX = "active:liquors:";
    private static final String RANKING_KEY_PREFIX = "ranking:";
    private static final int LIMIT = 10;

    public List<LiquorSummaryDto> getTrending(PeriodType period, UUID userId){

        String rankingKey = RANKING_KEY_PREFIX + period;
        Set<String> topIds = redisTemplate.opsForZSet().reverseRange(rankingKey, 0, LIMIT - 1);

        if (topIds != null && !topIds.isEmpty()) {
            List<Long> ids = topIds.stream().map(Long::valueOf).toList();
//            return // TODO Redis에서 가져옴.
        }

        // TODO Fallback - DB에서 조회
        List<Long> dbRankingIds = liquorRankingRepository.findTrendingLiquorIdsByPeriod(period);
        if (!dbRankingIds.isEmpty()) {

            Set<Long> likedLiquorIds = (userId != null)
                    ? liquorLikeRepository.findLikedLiquorIdsByUserIdAndLiquorIds(userId, dbRankingIds)
                    : Collections.emptySet();

            return liquorRepository.findByIdIn(dbRankingIds).stream()
                    .map(liquor -> {
                        boolean isLiked = likedLiquorIds.contains(liquor.getId());
                        String imageUrl = s3Service.getLiquorImageUrl(liquor.getImageKey()); // null-safe
                        return LiquorMapper.toSummaryDto(liquor, imageUrl, isLiked);
                    })
                    .toList();
        }

        return null; // TODO 모두 비정상 작동 시 주류 10개 최신순 return
    }

    /**
     * 스케줄러에서만 호출
     */
    public void calculateAndSaveRanking(Set<Long> activeIds, String rankingKey) {

        List<LiquorScoreDto> scores = liquorRepository.findScoresByIds(activeIds);

        redisTemplate.delete(rankingKey);

        // 상세 정보를 한 번에 긁어오기 위한 ID 리스트
        List<Long> topIds = scores.stream()
                .sorted(Comparator.comparing(LiquorScoreDto::calculateTotalScore).reversed())
                .limit(10) // TOP 10만 상세 캐싱
                .map(LiquorScoreDto::liquorId)
                .toList();

        List<LiquorSummaryDto> summaries = liquorRepository.findTrendingLiquorSummaries(topIds);

//        // ZSet에 저장
//        for (LiquorScoreDto score : scores) {
//            redisTemplate.opsForZSet().add(rankingKey, String.valueOf(score.liquorId()), totalScore);
//        }

        for (LiquorScoreDto score : scores) {
            // 점수 저장 (ZSet)
            redisTemplate.opsForZSet().add(rankingKey, String.valueOf(score.liquorId()), LiquorScoreDto.calculateTotalScore(score));

            // 상세 정보 캐싱 (TOP 10에 포함된 경우만 JSON으로 저장)
            summaries.stream()
                    .filter(dto -> dto.id().equals(score.liquorId()))
                    .findFirst();
//                    .ifPresent(this::cacheLiquorSummary);
        }
    }
}
