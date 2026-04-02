package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorScoreDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.enums.PeriodType;
import com.liquordb.mapper.LiquorMapper;
import com.liquordb.repository.LiquorLikeRepository;
import com.liquordb.repository.liquor.LiquorRankingRepository;
import com.liquordb.repository.liquor.LiquorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

    public List<LiquorSummaryDto> getTrending(PeriodType period, UUID userId) {

        String rankingKey = RANKING_KEY_PREFIX + period;
        Set<String> topIds = redisTemplate.opsForZSet().reverseRange(rankingKey, 0, LIMIT - 1);
        List<Long> ids;
        if (topIds != null && !topIds.isEmpty()) { // Redis에서 ID 조회
            ids = topIds.stream().map(Long::valueOf).toList();
        } else { // DB에서 ID 조회 (Fallback)
            ids = liquorRankingRepository.findTrendingLiquorIdsByPeriod(period);
        }

        if (!ids.isEmpty()) {
            Set<Long> likedLiquorIds = (userId != null)
                    ? liquorLikeRepository.findLikedLiquorIdsByUserIdAndLiquorIds(userId, ids)
                    : Collections.emptySet();

            return liquorRepository.findByIdIn(ids).stream()
                    .map(liquor -> {
                        boolean isLiked = likedLiquorIds.contains(liquor.getId());
                        String imageUrl = s3Service.getLiquorImageUrl(liquor.getImageKey()); // null-safe
                        return LiquorMapper.toSummaryDto(liquor, imageUrl, isLiked);
                    })
                    .toList();
        }

        return liquorRepository.findTopLatestLiquors(PageRequest.of(0, 10)).stream()
                .map(liquor -> {
                    Set<Long> likedLiquorIds = (userId != null)
                            ? liquorLikeRepository.findLikedLiquorIdsByUserIdAndLiquorIds(userId, ids)
                            : Collections.emptySet();
                    boolean isLiked = likedLiquorIds.contains(liquor.getId());
                    String imageUrl = s3Service.getLiquorImageUrl(liquor.getImageKey()); // null-safe
                    return LiquorMapper.toSummaryDto(liquor, imageUrl, isLiked);
                })
                .toList();
    }

    /**
     * 스케줄러에서만 호출
     */
    public void calculateAndSaveRanking(Set<Long> activeIds, String rankingKey) {

        List<LiquorScoreDto> scores = liquorRepository.findScoresByIds(activeIds);

        // Redis Sorted Set(ZSet)에 저장
        redisTemplate.delete(rankingKey); // 기존 랭킹 삭제

        for (LiquorScoreDto score : scores) {
            double totalScore = LiquorScoreDto.calculateTotalScore(score);
            redisTemplate.opsForZSet().add(rankingKey, String.valueOf(score.liquorId()), totalScore);
        }
    }
}
