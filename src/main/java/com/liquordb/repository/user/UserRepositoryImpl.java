package com.liquordb.repository.user;

import com.liquordb.entity.QUser;
import com.liquordb.entity.User;
import com.liquordb.enums.UserStatus;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

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
