package com.liquordb.service;

import com.liquordb.repository.LiquorLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 사용자별 주류 좋아요 목록 Redis 캐시 관리 서비스
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class LiquorLikeCacheService {

    private final StringRedisTemplate stringRedisTemplate;
    private final LiquorLikeRepository liquorLikeRepository;

    private static final String KEY_PREFIX = "user:likes:liquor:";
    private static final String EMPTY_MARKER = "-1";
    private static final Duration CACHE_TTL = Duration.ofDays(1);

    /**
     * 유저의 전체 좋아요 주류 ID Set 조회 (Cache-Aside)
     */
    public Set<Long> getAllLikedLiquorIds(UUID userId) {
        if (userId == null) {
            return Collections.emptySet();
        }

        String key = generateKey(userId);
        Boolean hasKey = stringRedisTemplate.hasKey(key);

        if (hasKey) {
            Set<String> members = stringRedisTemplate.opsForSet().members(key);
            if (members != null) {
                return members.stream()
                        .filter(id -> !EMPTY_MARKER.equals(id))
                        .map(Long::valueOf)
                        .collect(Collectors.toSet());
            }
        }

        // Cache Miss: DB 조회 후 Redis에 적재
        Set<Long> likedIds = liquorLikeRepository.findLikedLiquorIdsByUserId(userId);
        if (!likedIds.isEmpty()) {
            String[] strIds = likedIds.stream()
                    .map(String::valueOf)
                    .toArray(String[]::new);
            stringRedisTemplate.opsForSet().add(key, strIds);
        } else {
            stringRedisTemplate.opsForSet().add(key, EMPTY_MARKER);
        }
        stringRedisTemplate.expire(key, CACHE_TTL);

        return likedIds;
    }

    /**
     * 특정 주류 ID 목록 중 사용자가 좋아요를 누른 주류 ID들의 집합 반환
     */
    public Set<Long> getLikedLiquorIds(UUID userId, Collection<Long> liquorIds) {
        if (userId == null || liquorIds == null || liquorIds.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Long> allLikedIds = getAllLikedLiquorIds(userId);
        if (allLikedIds.isEmpty()) {
            return Collections.emptySet();
        }

        return liquorIds.stream()
                .filter(allLikedIds::contains)
                .collect(Collectors.toSet());
    }

    /**
     * 특정 주류 단건에 대한 좋아요 여부 확인
     */
    public boolean isLiked(UUID userId, Long liquorId) {
        if (userId == null || liquorId == null) {
            return false;
        }

        String key = generateKey(userId);
        Boolean hasKey = stringRedisTemplate.hasKey(key);

        if (hasKey) {
            Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, String.valueOf(liquorId));
            return Boolean.TRUE.equals(isMember);
        }

        // Cache Miss: 전체 로드 후 확인
        return getAllLikedLiquorIds(userId).contains(liquorId);
    }

    /**
     * 좋아요 추가 시 캐시 동기화
     */
    public void addLike(UUID userId, Long liquorId) {
        if (userId == null || liquorId == null) {
            return;
        }

        String key = generateKey(userId);
        if (stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.opsForSet().add(key, String.valueOf(liquorId));
            stringRedisTemplate.opsForSet().remove(key, EMPTY_MARKER);
            stringRedisTemplate.expire(key, CACHE_TTL);
        }
    }

    /**
     * 좋아요 취소 시 캐시 동기화
     */
    public void removeLike(UUID userId, Long liquorId) {
        if (userId == null || liquorId == null) {
            return;
        }

        String key = generateKey(userId);
        if (stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.opsForSet().remove(key, String.valueOf(liquorId));
            stringRedisTemplate.expire(key, CACHE_TTL);
        }
    }

    private String generateKey(UUID userId) {
        return KEY_PREFIX + userId.toString();
    }
}
