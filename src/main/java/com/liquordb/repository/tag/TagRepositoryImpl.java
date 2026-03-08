package com.liquordb.repository.tag;

import com.liquordb.entity.QTag;
import com.liquordb.entity.Tag;
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
public class TagRepositoryImpl implements CustomTagRepository {

    private final JPAQueryFactory queryFactory;
    private final QTag tag = QTag.tag;

    @Override
    public Page<Tag> findAll(TagSearchCondition condition) {
        int limit = condition.limit();
        int page = condition.page();
        List<Tag> content = queryFactory.selectFrom(tag)
                .where(
                        keywordContains(condition.keyword())
                )
                .orderBy(
                        getOrderSpecifier(condition.descending())
                )
                .offset((long) page * limit)
                .limit(limit)
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(tag.count())
                .from(tag)
                .where(
                        keywordContains(condition.keyword())
                );
        return PageableExecutionUtils.getPage(content, PageRequest.of(page, limit), countQuery::fetchOne);
    }

    /**
     * predicates
     */

    private BooleanExpression keywordContains(String keyword) {
        return keyword != null ? tag.name.contains(keyword) : null;
    }

    /**
     * OrderSpecifiers
     */

    private OrderSpecifier<?> getOrderSpecifier(boolean descending) {
        Order order = descending ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, tag.id);
    }
}
