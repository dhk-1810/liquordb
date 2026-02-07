package com.liquordb.service;

import com.liquordb.dto.LikeResponseDto;
import com.liquordb.entity.*;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.repository.CommentLikeRepository;
import com.liquordb.repository.CommentRepository;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public LikeResponseDto like(Long commentId, UUID userId) {

        if (commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId)) {
            return new LikeResponseDto(true, commentLikeRepository.countByComment_Id(commentId));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new LiquorNotFoundException(commentId));

        CommentLike commentLike = CommentLike.create(user, comment);
        commentLikeRepository.save(commentLike);
        long likeCount = commentLikeRepository.countByComment_Id(commentId);
        return new LikeResponseDto(true, likeCount);
    }

    @Transactional
    public LikeResponseDto cancelLike(Long commentId, UUID userId) {
        commentLikeRepository.findByCommentIdAndUser_Id(commentId, userId)
                .ifPresent(commentLikeRepository::delete);

        long likeCount = commentLikeRepository.countByComment_Id(commentId);
        return new LikeResponseDto(false, likeCount);
    }

}
