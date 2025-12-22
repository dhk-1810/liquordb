package com.liquordb.service;

import com.liquordb.dto.comment.CommentLikeResponseDto;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.entity.CommentLike;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
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
        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.BANNED)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        CommentLike existingCommentLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId)
                .orElse(null);

        if (existingCommentLike != null) {
            commentLikeRepository.delete(existingCommentLike);
            return null;
        } else {
            CommentLike newLike = CommentLike.create(user, comment);
            commentLikeRepository.save(newLike);
            return CommentLikeResponseDto.toDto(newLike);
        }
    }

}
