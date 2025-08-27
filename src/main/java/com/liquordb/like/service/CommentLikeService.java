package com.liquordb.like.service;

import com.liquordb.like.dto.CommentLikeResponseDto;
import com.liquordb.like.dto.ReviewLikeResponseDto;
import com.liquordb.like.entity.CommentLike;
import com.liquordb.like.repository.CommentLikeRepository;
import com.liquordb.review.entity.Comment;
import com.liquordb.review.repository.CommentRepository;
import com.liquordb.user.entity.User;
import com.liquordb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;

    // 좋아요 토글 (누르기/취소)
    @Transactional
    public CommentLikeResponseDto toggleLike(Long userId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글입니다."));

        CommentLike existing = commentLikeRepository.findByUserIdAndCommentId(userId, commentId)
                .orElse(null);

        if (existing != null) {
            commentLikeRepository.delete(existing);
            return buildResponse(
                    existing.getId(),
                    userId,
                    commentId,
                    null);
        } else {
            CommentLike newLike = CommentLike.builder()
                    .user(user)
                    .comment(comment)
                    .likedAt(LocalDateTime.now())
                    .build();

            CommentLike saved = commentLikeRepository.save(newLike);

            return buildResponse(
                    saved.getId(),
                    userId,
                    commentId,
                    saved.getLikedAt());
        }
    }

    @Transactional(readOnly = true)
    public long countLikes(Long targetId) {
        return commentLikeRepository.countByCommentId(targetId);
    }

    //
    private CommentLikeResponseDto buildResponse(Long id, Long userId, Long commentId, LocalDateTime likedAt) {
        return CommentLikeResponseDto.builder()
                .id(id)
                .userId(userId)
                .commentId(commentId)
                .likedAt(likedAt)
                .build();
    }
}
