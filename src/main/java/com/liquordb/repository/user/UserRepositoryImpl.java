package com.liquordb.repository.user;

import com.liquordb.dto.user.UserActivityCountDto;
import com.liquordb.entity.Comment;
import com.liquordb.entity.QComment;
import com.liquordb.entity.QCommentLike;
import com.liquordb.entity.QLiquorLike;
import com.liquordb.entity.QReview;
import com.liquordb.entity.QReviewLike;
import com.liquordb.entity.QUser;
import com.liquordb.entity.Review;
import com.liquordb.entity.User;
import com.liquordb.enums.UserStatus;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    @Override
    public Page<User> findAll(UserSearchCondition condition) {
        int limit = condition.limit();
        int page = condition.page();
        List<User> content = queryFactory.selectFrom(user)
                .where(
                        usernameContains(condition.username()),
                        emailContains(condition.email()),
                        statusEq(condition.status())

                )
                .orderBy(
                        getOrderSpecifier(condition.descending())
                )
                .offset((long) page * limit)
                .limit(limit)
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        usernameContains(condition.username()),
                        emailContains(condition.email()),
                        statusEq(condition.status())
                );
        return PageableExecutionUtils.getPage(content, PageRequest.of(page, limit), countQuery::fetchOne);
    }

    @Override
    public UserActivityCountDto getUserActivityCounts(UUID userId) {
        QReview review = QReview.review;
        QComment comment = QComment.comment;
        QLiquorLike liquorLike = QLiquorLike.liquorLike;
        QReviewLike reviewLike = QReviewLike.reviewLike;
        QCommentLike commentLike = QCommentLike.commentLike;

        Expression<Long> reviewCountSub = JPAExpressions.select(review.count())
                .from(review)
                .where(review.user.id.eq(userId), review.status.eq(Review.ReviewStatus.ACTIVE));

        Expression<Long> commentCountSub = JPAExpressions.select(comment.count())
                .from(comment)
                .where(comment.user.id.eq(userId), comment.status.eq(Comment.CommentStatus.ACTIVE));

        Expression<Long> likedLiquorCountSub = JPAExpressions.select(liquorLike.count())
                .from(liquorLike)
                .where(liquorLike.user.id.eq(userId), liquorLike.liquor.isDeleted.isFalse());

        Expression<Long> likedReviewCountSub = JPAExpressions.select(reviewLike.count())
                .from(reviewLike)
                .where(reviewLike.user.id.eq(userId), reviewLike.review.status.eq(Review.ReviewStatus.ACTIVE));

        Expression<Long> likedCommentCountSub = JPAExpressions.select(commentLike.count())
                .from(commentLike)
                .where(commentLike.user.id.eq(userId), commentLike.comment.status.eq(Comment.CommentStatus.ACTIVE));

        UserActivityCountDto result = queryFactory
                .select(Projections.constructor(UserActivityCountDto.class,
                        reviewCountSub,
                        commentCountSub,
                        likedLiquorCountSub,
                        likedReviewCountSub,
                        likedCommentCountSub
                ))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();

        return result != null ? result : UserActivityCountDto.empty();
    }

    /**
     * predicates
     */

    private BooleanExpression usernameContains(String username) {
        return username != null ? user.username.contains(username) : null;
    }

    private BooleanExpression emailContains(String email) {
        return email != null ? user.email.contains(email) : null;
    }

    private BooleanExpression statusEq(UserStatus status) {
        return status != null ? user.status.eq(status) : null;
    }

    /**
     * OrderSpecifiers
     */

    private OrderSpecifier<?> getOrderSpecifier(boolean descending) {
        Order order = descending ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, user.id);
    }
}
