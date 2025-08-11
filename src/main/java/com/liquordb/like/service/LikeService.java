package com.liquordb.like.service;

import com.liquordb.liquor.entity.Liquor;
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

    // 좋아요 누르기
    @Transactional
    public LikeResponseDto like(Long userId, LikeRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        validateTargetExists(request.getTargetId(), request.getTargetType());

        Optional<Like> existing = likeRepository.findByUserIdAndTargetIdAndTargetType(
                userId, request.getTargetId(), request.getTargetType());

        if (existing.isPresent()) {
            throw new IllegalStateException("Already liked");
        }

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

    // 좋아요 취소
    @Transactional
    public void unlike(Long userId, LikeRequestDto request) {
        Like like = likeRepository.findByUserIdAndTargetIdAndTargetType(
                userId, request.getTargetId(), request.getTargetType()
        ).orElseThrow(() -> new IllegalArgumentException("Like not found"));

        likeRepository.delete(like);
    }

    // 좋아요 카운트
    @Transactional(readOnly = true)
    public long countLikes(Long targetId, LikeTargetType type) {
        return likeRepository.countByTargetIdAndTargetType(targetId, type);
    }

    private void validateTargetExists(Long targetId, LikeTargetType type) {
        switch (type) {
            case LIQUOR -> {
                if (!liquorRepository.existsById(targetId)) {
                    throw new RuntimeException("Liquor not found");
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
