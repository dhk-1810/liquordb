package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorLikeResponseDto;
import com.liquordb.entity.LiquorLike;
import com.liquordb.repository.LiquorLikeRepository;
import com.liquordb.entity.Liquor;
import com.liquordb.repository.LiquorRepository;
import com.liquordb.entity.User;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiquorLikeService {

    private final LiquorRepository liquorRepository;
    private final LiquorLikeRepository liquorLikeRepository;
    private final UserRepository userRepository;

    // 좋아요 토글 (누르기/취소)
    @Transactional
    public LiquorLikeResponseDto toggleLike(UUID userId, Long liquorId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        Liquor liquor = liquorRepository.findById(liquorId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 주류입니다."));

        LiquorLike existing = liquorLikeRepository.findByUserIdAndLiquorId(user.getId(), liquor.getId())
                .orElse(null);

        if (existing != null) { // 이미 좋아요 눌려있으면 취소
            liquorLikeRepository.delete(existing);
            return buildResponse(
                    existing.getId(),
                    userId,
                    liquorId,
                    null);
        } else {
            LiquorLike newLike = LiquorLike.builder()
                            .user(user)
                            .liquor(liquor)
                            .likedAt(LocalDateTime.now())
                            .build();

            LiquorLike saved = liquorLikeRepository.save(newLike);

            return buildResponse(
                    saved.getId(),
                    userId,
                    liquorId,
                    saved.getLikedAt());
        }
    }


    // 좋아요 카운트
    @Transactional(readOnly = true)
    public long liquorCountLikes(Long liquorId) {
        return liquorLikeRepository.countByLiquorId(liquorId);
    }

    // ?
    private LiquorLikeResponseDto buildResponse(Long id, UUID userId, Long liquorId, LocalDateTime likedAt) {
        return LiquorLikeResponseDto.builder()
                .id(id)
                .userId(userId)
                .liquorId(liquorId)
                .likedAt(likedAt)
                .build();
    }
}
