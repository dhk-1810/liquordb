package com.liquordb.repository.comment;

import com.liquordb.entity.Comment;
import com.liquordb.entity.QComment;
import com.liquordb.repository.comment.condition.CommentListGetCondition;
import com.liquordb.repository.comment.condition.CommentSearchCondition;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

// TODO 복합 인덱스 생성 (likeCount, id)

@RequiredArgsConstructor
@Repository
public class CommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory queryFactory;
    private final QComment comment = QComment.comment;

    // 특정 리뷰에 달린 댓글 조회
    @Override
    public Slice<Comment> findByReviewId(CommentListGetCondition condition) {

        int limit = condition.limit();
        List<Comment> content = queryFactory.selectFrom(comment)
                .where(
                        reviewIdEq(condition.reviewId()),
                        statusEq(condition.status()),
                        cursorCondition(condition.cursor(), condition.idAfter(), condition.useId(), condition.descending())
                )
                .orderBy(
                        getOrderSpecifier(condition.descending(), condition.useId()),
                        getTieBreakerOrder(condition.descending())
                )
                .limit(limit + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > limit) {
            content.remove(limit); // 초과분 제거
            hasNext = true;
        }

        return new SliceImpl<>(content, PageRequest.ofSize(limit), hasNext);
    }

    // 특정 유저가 작성한 댓글 조회
    @Override
    public Slice<Comment> findByUserId(CommentListGetCondition condition) {

        int limit = condition.limit();
        List<Comment> content = queryFactory.selectFrom(comment)
                .where(
                        userIdEq(condition.userId()),
                        statusEq(condition.status()),
                        cursorCondition(condition.cursor(), condition.descending())
                )
                .orderBy(
                        getOrderSpecifier(condition.descending(), true)
                )
                .limit(limit + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > limit) {
            content.remove(limit);
            hasNext = true;
        }

        return new SliceImpl<>(content, PageRequest.ofSize(limit), hasNext);
    }

    @Override
    public Page<Comment> findAll(CommentSearchCondition condition) {

        int limit = condition.limit();
        int page = condition.page();
        List<Comment> content = queryFactory.selectFrom(comment)
                .where(
                        usernameContains(condition.username()),
                        statusEq(condition.commentStatus())
                )
                .orderBy(
                        getOrderSpecifier(condition.descending(), true)
                )
                .offset((long) page * limit)
                .limit(limit)
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .where(
                        usernameContains(condition.username()),
                        statusEq(condition.commentStatus())
                );
        return PageableExecutionUtils.getPage(content, PageRequest.of(page, limit), countQuery::fetchOne);
    }

    /**
     * Predicates
     */

    private Predicate cursorCondition(Long cursor, Long idAfter, boolean useId, boolean descending) {
        if (cursor == null) {
            return null; // 첫 페이지 조회
        }

        // 주 커서
        if (useId) { // ID로 정렬
            if (descending) {
                return comment.id.lt(cursor);
            } else {
                return comment.id.gt(cursor);
            }
        } else { // likeCount로 정렬
            if (descending) {
                return comment.likeCount.lt(cursor)
                        .or(
                                comment.likeCount.eq(cursor).and(comment.id.lt(idAfter))
                        );
            } else {
                return comment.likeCount.gt(cursor)
                        .or(
                                comment.likeCount.eq(cursor).and(comment.id.gt(idAfter))
                        );
            }
        }
    }

    private Predicate cursorCondition(Long cursor, boolean descending) {
        if (cursor == null) {
            return null; // 첫 페이지 조회
        }

        // 주 커서
        if (descending) {
            return comment.id.lt(cursor);
        } else {
            return comment.id.gt(cursor);
        }
    }

    private Predicate reviewIdEq(Long reviewId) {
        return reviewId != null ? comment.review.id.eq(reviewId) : null;
    }

    private Predicate userIdEq(UUID userId) {
        return userId != null ? comment.user.id.eq(userId) : null;
    }

    private Predicate statusEq(Comment.CommentStatus status) {
        return status != null ? comment.status.eq(status) : null;
    }

    private Predicate usernameContains(String username) {
        return username != null ? comment.user.username.contains(username) : null;
    }

    /**
     * OrderSpecifiers
     */

    private OrderSpecifier<?> getOrderSpecifier(boolean descending, boolean useId) {
        Order order = descending ? Order.DESC : Order.ASC;
        if (useId) {
            return new OrderSpecifier<>(order, comment.id);
        } else {
            return new OrderSpecifier<>(order, comment.likeCount);
        }
    }

    private OrderSpecifier<?> getTieBreakerOrder(boolean descending) {
        Order order = descending ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, comment.id);
    }
}
