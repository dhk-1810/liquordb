package com.liquordb.service;

import com.liquordb.entity.Comment;
import com.liquordb.entity.CommentLike;
import com.liquordb.entity.User;
import com.liquordb.event.CommentLikeEvent;
import com.liquordb.exception.comment.CommentLikeAlreadyExistsException;
import com.liquordb.exception.comment.CommentLikeNotFoundException;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.repository.CommentLikeRepository;
import com.liquordb.repository.comment.CommentRepository;
import com.liquordb.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void like(Long commentId, UUID userId) {

        if (commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId)) {
            throw new CommentLikeAlreadyExistsException(commentId, userId);
        }

        User user = userRepository.getReferenceById(userId);
        Comment comment = commentRepository.getReferenceById(commentId);
        CommentLike commentLike = CommentLike.create(user, comment);

        try {
            commentLikeRepository.save(commentLike);
        } catch (DataIntegrityViolationException e) {
            throw new CommentNotFoundException(commentId);
        }

        eventPublisher.publishEvent(new CommentLikeEvent(commentId, true));
    }

    @Transactional
    public void cancelLike(Long commentId, UUID userId) {

        CommentLike commentLike = commentLikeRepository.findByComment_IdAndUser_Id(commentId, userId)
                .orElseThrow(() -> new CommentLikeNotFoundException(commentId, userId));

        commentLikeRepository.delete(commentLike);
        eventPublisher.publishEvent(new CommentLikeEvent(commentId, false));
    }

}
