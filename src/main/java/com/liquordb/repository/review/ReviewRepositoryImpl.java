package com.liquordb.repository.review;

import com.liquordb.entity.QReview;
import com.liquordb.entity.Review;
import com.liquordb.enums.SortReviewBy;
import com.liquordb.repository.review.condition.ReviewListGetCondition;
import com.liquordb.repository.review.condition.ReviewSearchCondition;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.liquordb.enums.SortReviewBy.REVIEW_ID;

@RequiredArgsConstructor
@Repository
public class ReviewRepositoryImpl implements CustomReviewRepository {

    private final JPAQueryFactory queryFactory;
    private final QReview review = QReview.review;

    @Override
    public Slice<Review> findByLiquorId(ReviewListGetCondition condition) {

        int limit = condition.limit();
        List<Review> content = queryFactory.selectFrom(review)
                .where(
                        liquorIdEq(condition.liquorId()),
                        statusEq(condition.status()),
                        ratingEq(condition.rating()),
                        cursorCondition(condition.cursor(), condition.idAfter(), condition.sortBy(), condition.descending())
                )
                .orderBy(
                        getOrderSpecifier(condition.descending(), condition.sortBy()),
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

    @Override
    public Slice<Review> findByUserId(ReviewListGetCondition condition) {

        int limit = condition.limit();
        List<Review> content = queryFactory.selectFrom(review)
                .where(
                        userIdEq(condition.userId()),
                        statusEq(condition.status()),
                        cursorCondition(condition.cursor(), condition.descending())
                )
                .orderBy(
                        getOrderSpecifier(condition.descending(), condition.sortBy())
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
    public Page<Review> findAll(ReviewSearchCondition condition) {

        int limit = condition.limit();
        int page = condition.page();
        List<Review> content = queryFactory.selectFrom(review)
                .where(
                        usernameContains(condition.username()),
                        statusEq(condition.reviewStatus())
                )
                .orderBy(
                        getOrderSpecifier(condition.descending(), REVIEW_ID)
                )
                .offset((long) page * limit)
                .limit(limit)
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(
                        usernameContains(condition.username()),
                        statusEq(condition.reviewStatus())
                );
        return PageableExecutionUtils.getPage(content, PageRequest.of(page, limit), countQuery::fetchOne);
    }

    /**
     * Predicates
     */

    private Predicate cursorCondition(Long cursor, Long idAfter, SortReviewBy sortBy, boolean descending) {
        if (cursor == null) {
            return null; // 첫 페이지 조회
        }

        // 주 커서
        switch (sortBy) {
            case REVIEW_ID -> {
                if (descending) {
                    return review.id.lt(cursor);
                } else {
                    return review.id.gt(cursor);
                }
            }
            case LIKE_COUNT -> {
                if (descending) {
                    return review.likeCount.lt(cursor)
                            .or(review.likeCount.eq(cursor).and(review.id.lt(idAfter)));
                } else {
                    return review.likeCount.gt(cursor)
                            .or(review.likeCount.eq(cursor).and(review.id.gt(idAfter)));
                }
            }
            case COMMENT_COUNT -> {
                if (descending) {
                    return review.commentCount.lt(cursor)
                            .or(review.commentCount.eq(cursor).and(review.id.lt(idAfter)));
                } else {
                    return review.commentCount.gt(cursor)
                            .or(review.commentCount.eq(cursor).and(review.id.gt(idAfter)));
                }
            }
            default ->  {
                return null;
            }
        }
    }

    private Predicate cursorCondition(Long cursor, boolean descending) {
        if (cursor == null) {
            return null; // 첫 페이지 조회
        }

        // 주 커서
        if (descending) {
            return review.id.lt(cursor);
        } else {
            return review.id.gt(cursor);
        }
    }

    private Predicate liquorIdEq(Long liquorId) {
        return liquorId != null ? review.liquor.id.eq(liquorId) : null;
    }

    private Predicate ratingEq(Short rating) {
        return rating != null ? review.rating.eq(rating) : null;
    }

    private Predicate userIdEq(UUID userId) {
        return userId != null ? review.user.id.eq(userId) : null;
    }

    private Predicate statusEq(Review.ReviewStatus status) {
        return status != null ? review.status.eq(status) : null;
    }

    private Predicate usernameContains(String username) {
        return username != null ? review.user.username.contains(username) : null;
    }

    /**
     * OrderSpecifiers
     */

    private OrderSpecifier<?> getOrderSpecifier(boolean descending, SortReviewBy sortBy) {
        Order order = descending ? Order.DESC : Order.ASC;
        switch (sortBy) {
            case REVIEW_ID -> {
                return new OrderSpecifier<>(order, review.id);
            }
            case LIKE_COUNT -> {
                return new OrderSpecifier<>(order, review.likeCount);
            }
            case COMMENT_COUNT -> {
                return new OrderSpecifier<>(order, review.commentCount);
            }
            default -> {
                return null;
            }
        }
    }

    private OrderSpecifier<?> getTieBreakerOrder(boolean descending) {
        Order order = descending ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, review.id);
    }
}
