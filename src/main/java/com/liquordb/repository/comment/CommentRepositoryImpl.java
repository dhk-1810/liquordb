package com.liquordb.repository.comment;

import com.liquordb.entity.Comment;
import com.liquordb.entity.QComment;
import lombok.RequiredArgsConstructor;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class CommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QComment comment = QComment.comment;


    @Override
    public Slice<Comment> findByReview_IdAndStatus(Long reviewId, Comment.CommentStatus status, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Comment> findByUserIdAndStatus(UUID userId, Comment.CommentStatus statuses, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Comment> findAllByOptionalFilters(UUID userId, Comment.CommentStatus status, Pageable pageable) {
        return null;
    }
}
