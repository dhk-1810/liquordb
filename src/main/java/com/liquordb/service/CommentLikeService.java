package com.liquordb.service;

import com.liquordb.dto.LikeResponseDto;
import com.liquordb.entity.*;
import com.liquordb.event.CommentLikeEvent;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.repository.CommentLikeRepository;
import com.liquordb.repository.comment.CommentRepository;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public LikeResponseDto like(Long commentId, UUID userId) {

        if (commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId)) {
            return new LikeResponseDto(true, commentLikeRepository.countByComment_Id(commentId));
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

        long likeCount = commentLikeRepository.countByComment_Id(commentId);
        return new LikeResponseDto(true, likeCount);
    }

    @Transactional
    public LikeResponseDto cancelLike(Long commentId, UUID userId) {
        commentLikeRepository.findByCommentIdAndUser_Id(commentId, userId)
                .ifPresent(commentLikeRepository::delete);

        eventPublisher.publishEvent(new CommentLikeEvent(commentId, false));
        long likeCount = commentLikeRepository.countByComment_Id(commentId);
        return new LikeResponseDto(false, likeCount);
    }

}
