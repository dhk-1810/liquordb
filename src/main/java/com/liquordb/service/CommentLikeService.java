package com.liquordb.service;

import com.liquordb.dto.comment.CommentLikeResponseDto;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.entity.CommentLike;
import com.liquordb.entity.UserStatus;
import com.liquordb.exception.NotFoundException;
import com.liquordb.mapper.CommentMapper;
import com.liquordb.repository.CommentLikeRepository;
import com.liquordb.entity.Comment;
import com.liquordb.repository.CommentRepository;
import com.liquordb.entity.User;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;

    // 좋아요 토글 (누르기/취소)
    @Transactional
    public CommentLikeResponseDto toggleLike(UUID userId, Long commentId) {
        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));

        Optional<CommentLike> optionalCommentLike
                = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);

        if (optionalCommentLike.isPresent()) {
            commentLikeRepository.delete(optionalCommentLike.get());
            return buildResponse(
                    null, // TODO
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

    // 특정 댓글에 달린 좋아요 수
    @Transactional(readOnly = true)
    public long countByCommentId(Long commentId) {
        return commentLikeRepository.countByCommentId(commentId);
    }

    // 특정 유저가 좋아요 누른 댓글 수
    @Transactional(readOnly = true)
    public long countByUserId(User user) {
        return commentLikeRepository.countByUserAndCommentIsHiddenFalse(user);
    }

    // 유저가 좋아요 누른 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentSummaryDtosByUserId(UUID userId) {
        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        return commentLikeRepository.findByUserAndCommentIsHiddenFalse(user).stream()
                .map(commentLike -> CommentMapper.toDto(commentLike.getComment()))
                .toList();
    }

    // DTO 생성
    private CommentLikeResponseDto buildResponse(Long id, UUID userId, Long commentId, LocalDateTime likedAt) {
        return CommentLikeResponseDto.builder()
                .id(id)
                .userId(userId)
                .commentId(commentId)
                .likedAt(likedAt)
                .build();
    }
}
