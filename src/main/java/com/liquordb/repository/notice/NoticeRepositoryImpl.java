package com.liquordb.repository.notice;

import com.liquordb.entity.Notice;
import com.liquordb.entity.QNotice;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
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
public class NoticeRepositoryImpl implements CustomNoticeRepository {

    private final QNotice notice = QNotice.notice;
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Notice> findAll(NoticeListGetCondition condition) {
        int limit = condition.limit();
        int page = condition.page();

        List<Notice> content = queryFactory.selectFrom(notice)
                .where(
                        isDeletedEq(condition.deleted())
                )
                .orderBy(
                        notice.isPinned.desc(),
                        notice.id.desc()
                )
                .offset((long) page * limit)
                .limit(limit)
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(notice.count())
                .from(notice)
                .where(
                        isDeletedEq(condition.deleted())
                );
        return PageableExecutionUtils.getPage(content, PageRequest.of(page, limit), countQuery::fetchOne);
    }

    private Predicate isDeletedEq(boolean deleted) {
        return notice.isDeleted.eq(deleted);
    }

    /**
     * OrderSpecifiers
     */

    private OrderSpecifier<?> getOrderSpecifier(boolean descending) {
        Order order = descending ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, notice.id);
    }

}