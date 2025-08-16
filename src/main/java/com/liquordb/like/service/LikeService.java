package com.liquordb.like.service;

import com.liquordb.liquor.repository.LiquorRepository;
import com.liquordb.review.repository.CommentRepository;
import com.liquordb.like.dto.LikeRequestDto;
import com.liquordb.like.dto.LikeResponseDto;
import com.liquordb.like.entity.Like;
import com.liquordb.like.entity.LikeTargetType;
import com.liquordb.like.repository.LikeRepository;
import com.liquordb.review.repository.ReviewRepository;
import com.liquordb.user.entity.User;
import com.liquordb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final LiquorRepository liquorRepository;

    // 좋아요 토글 (누르기/취소)
    @Transactional
    public LikeResponseDto toggleLike(Long userId, LikeRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        validateTargetExists(request.getTargetId(), request.getTargetType());

        Optional<Like> existing = likeRepository.findByUserIdAndTargetIdAndTargetType(
                userId, request.getTargetId(), request.getTargetType());

        if (existing.isPresent()) { // 이미 좋아요 → 취소
            likeRepository.delete(existing.get());
            return LikeResponseDto.builder()
                    .id(existing.get().getId())
                    .userId(userId)
                    .targetId(request.getTargetId())
                    .targetType(request.getTargetType())
                    .likedAt(null) // 좋아요 해제니까 null
                    .build();
        } else { // 좋아요 추가
            Like like = Like.builder()
                    .user(user)
                    .targetId(request.getTargetId())
                    .targetType(request.getTargetType())
                    .likedAt(LocalDateTime.now())
                    .build();

            Like saved = likeRepository.save(like);

            return LikeResponseDto.builder()
                    .id(saved.getId())
                    .userId(userId)
                    .targetId(saved.getTargetId())
                    .targetType(saved.getTargetType())
                    .likedAt(saved.getLikedAt())
                    .build();
        }
    }

    // 좋아요 카운트
    @Transactional(readOnly = true)
    public long countLikes(Long targetId, LikeTargetType type) {
        return likeRepository.countByTargetIdAndTargetType(targetId, type);
    }

    // 검증
    private void validateTargetExists(Long targetId, LikeTargetType type) {
        switch (type) {
            case LIQUOR -> {
                if (!liquorRepository.existsById(targetId)) {
                    throw new RuntimeException("존재하지 않는 주류입니다.");
                }
            }
            case REVIEW -> {
                if (!reviewRepository.existsById(targetId)) {
                    throw new IllegalArgumentException("존재하지 않는 리뷰입니다. id=" + targetId);
                }
            }
            case COMMENT -> {
                if (!commentRepository.existsById(targetId)) {
                    throw new IllegalArgumentException("존재하지 않는 댓글입니다. id=" + targetId);
                }
            }
            default -> throw new IllegalArgumentException("지원하지 않는 LikeTargetType입니다: " + type);
        }
    }
}
