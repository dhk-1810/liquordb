package com.liquordb.repository.review;

import com.liquordb.entity.QReview;
import com.liquordb.entity.Review;
import com.liquordb.enums.CommentSortBy;
import com.liquordb.enums.ReviewSortBy;
import com.liquordb.repository.review.condition.ReviewListGetCondition;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.liquordb.enums.ReviewSortBy.COMMENT_COUNT;
import static com.liquordb.enums.ReviewSortBy.REVIEW_ID;

// TODO 평점 필터링

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
    public Page<Review> findAll(String username, Review.ReviewStatus status, Pageable pageable) {
        return null; // TODO
    }

    /**
     * Predicates
     */

    private Predicate cursorCondition(Long cursor, Long idAfter, ReviewSortBy sortBy, boolean descending) {
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

    private Predicate userIdEq(UUID userId) {
        return userId != null ? review.user.id.eq(userId) : null;
    }

    private Predicate statusEq(Review.ReviewStatus status) {
        return status != null ? review.status.eq(status) : null;
    }

    /**
     * OrderSpecifiers
     */

    private OrderSpecifier<?> getOrderSpecifier(boolean descending, ReviewSortBy sortBy) {
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
