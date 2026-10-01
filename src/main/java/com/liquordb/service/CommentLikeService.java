package com.liquordb.service;

import com.liquordb.entity.Comment;
import com.liquordb.entity.CommentLike;
import com.liquordb.entity.User;
import com.liquordb.exception.comment.CommentLikeAlreadyExistsException;
import com.liquordb.exception.comment.CommentLikeNotFoundException;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.exception.comment.SelfCommentLikeException;
import com.liquordb.repository.CommentLikeRepository;
import com.liquordb.repository.comment.CommentRepository;
import com.liquordb.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentLikeService {

    private static final String COMMENT_LIKE_MESSAGE_SUFFIX = "님이 내 댓글을 좋아합니다.";

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService; // 단방향 참조

    @Transactional
    public void like(Long commentId, UUID userId) {

        if (commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId)) {
            throw new CommentLikeAlreadyExistsException(commentId, userId);
        }

        // 작성자
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Comment comment = commentRepository.findByIdWAndStatusWithUser(commentId, Comment.CommentStatus.ACTIVE)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (comment.getUser().getId().equals(userId)) {
            throw new SelfCommentLikeException(commentId, userId);
        }

        CommentLike commentLike = CommentLike.create(user, comment);

        try {
            commentLikeRepository.saveAndFlush(commentLike);
        } catch (DataIntegrityViolationException e) {
            throw new CommentNotFoundException(commentId);
        }

        commentRepository.updateLikeCount(commentId, 1);
        notificationService.sendNotification(comment.getUser().getId(), user.getUsername() + COMMENT_LIKE_MESSAGE_SUFFIX);
    }

    @Transactional
    public void cancelLike(Long commentId, UUID userId) {

        CommentLike commentLike = commentLikeRepository.findByComment_IdAndUser_Id(commentId, userId)
                .orElseThrow(() -> new CommentLikeNotFoundException(commentId, userId));

        commentLikeRepository.delete(commentLike);
        commentLikeRepository.flush();
        commentRepository.updateLikeCount(commentId, -1);
    }

}
