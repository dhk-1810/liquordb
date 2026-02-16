package com.liquordb.repository.comment;

import com.liquordb.dto.comment.request.CommentListGetRequest;
import com.liquordb.dto.comment.request.CommentSearchRequest;
import com.liquordb.entity.Comment;
import com.liquordb.entity.QComment;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class CommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory queryFactory;
    private final QComment comment = QComment.comment;


    // 특정 리뷰에 달린 댓글 조회
    @Override
    public Slice<Comment> findByReviewIdAndStatus(Long reviewId, CommentListGetRequest request) {
        queryFactory.selectFrom(comment)
                .where(

                )
                .orderBy()
                .limit()
                .fetch();
        return new SliceImpl<>(contents, PageRequest.ofSize(limit), hasNext);
    }

    // 특정 유저가 작성한 댓글 조회
    @Override
    public Page<Comment> findByUserIdAndStatus(UUID userId, CommentListGetRequest request) {
        return null;
    }

    @Override
    public Page<Comment> findAll(CommentSearchRequest request) { // TODO Condition 도입
        queryFactory.selectFrom(comment)
                .where(
                        usernameEq(),
                        statusEq(status)
                )
                .orderBy()
                .limit()
                .fetch();
    }



    private Predicate cursorCondition(String cursor, LocalDateTime after, boolean ascending, boolean useRating) {
        if (cursor == null) {
            return null; // 첫 페이지 조회
        }

        // 주 커서 필드 (rating 또는 createdAt)
        if (useRating) {
            // rating 기준
            if (ascending) { // ASC: rating이 커지거나 (같으면) createdAt이 커지는 경우
                return qReview.rating.gt(Integer.parseInt(cursor)) // gt = greater than
                        .or(
                                qReview.rating.eq(Integer.parseInt(cursor)).and(qReview.createdAt.gt(after))
                        );
            } else { // DESC: rating이 작아지거나 (같으면) createdAt이 작아지는 경우
                return qReview.rating.lt(Integer.parseInt(cursor)) // lt = less than
                        .or(
                                qReview.rating.eq(Integer.parseInt(cursor)).and(qReview.createdAt.lt(after))
                        );
            }
        } else {
            // createdAt 기준
            if (ascending) { // ASC: createdAt이 커지는 경우 (오래된순)
                return qReview.createdAt.gt(after);
            } else { // DESC: createdAt이 작아지는 경우 (최신순)
                return qReview.createdAt.lt(after);
            }
        }
    }

    private Predicate usernameEq(String username) {
        return username != null ? comment.user.username.eq(username) : null;
    }

    private Predicate statusEq(Comment.CommentStatus status) {
        return status != null ? comment.status.eq(status) : null;
    }



    private static OrderSpecifier<?>[] orderByExpressions(String sortDirection, String sortBy) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        orderSpecifiers.add(orderByCursor(sortDirection, sortBy));
        final var orderById = new OrderSpecifier<>(Order.valueOf(sortDirection), playlist.id);
        orderSpecifiers.add(orderById);

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }

    private OrderSpecifier<?> createdAtOrder(boolean ascending) {
        Order order = ascending ? Order.ASC : Order.DESC;
        return new OrderSpecifier<>(order, bur.score);
    }

    private OrderSpecifier<?> idOrder(boolean ascending) {
        Order order = ascending ? Order.ASC : Order.DESC;
        return new OrderSpecifier<>(order, bur.id);
    }
}
