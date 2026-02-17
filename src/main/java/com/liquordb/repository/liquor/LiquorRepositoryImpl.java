package com.liquordb.repository.liquor;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import com.liquordb.entity.QLiquor;
import com.liquordb.enums.SortLiquorBy;
import com.liquordb.repository.liquor.condition.LiquorSearchCondition;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class LiquorRepositoryImpl implements CustomLiquorRepository{

    private final JPAQueryFactory queryFactory;
    private final QLiquor liquor = QLiquor.liquor;

    @Override
    public Slice<Liquor> findAll(LiquorSearchCondition condition) {

        int limit = condition.limit();
        List<Liquor> content = queryFactory.selectFrom(liquor)
                .where(
                        categoryEq(condition.category()),
                        subcategoryEq(condition.subcategory()),
                        keywordContains(condition.keyword()),
                        isDeletedEq(condition.searchDeleted()),
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
            content.remove(limit);
            hasNext = true;
        }

        return new SliceImpl<>(content, PageRequest.ofSize(limit), hasNext);
    }

    /**
     * Predicates
     */

    private Predicate cursorCondition(Object cursor, Long idAfter, SortLiquorBy sortBy, boolean descending) {
        if (cursor == null) {
            return null; // 첫 페이지 조회
        }
        // 주 커서
        switch (sortBy) {
            case LIQUOR_ID -> {
                Long idCursor = (Long) cursor;
                if (descending) {
                    return liquor.id.lt(idCursor);
                } else {
                    return liquor.id.gt(idCursor);
                }
            }
            case LIKE_COUNT -> {
                Long likeCountCursor = (Long) cursor;
                if (descending) {
                    return liquor.likeCount.lt(likeCountCursor)
                            .or(liquor.likeCount.eq(likeCountCursor).and(liquor.id.lt(idAfter)));
                } else {
                    return liquor.likeCount.gt(likeCountCursor)
                            .or(liquor.likeCount.eq(likeCountCursor).and(liquor.id.gt(idAfter)));
                }
            }
            case AVERAGE_RATING -> {
                Double avgRatingCursor = (Double) cursor;
                if (descending) {
                    return liquor.averageRating.lt(avgRatingCursor)
                            .or(liquor.averageRating.eq(avgRatingCursor).and(liquor.id.lt(idAfter)));
                } else {
                    return liquor.averageRating.gt(avgRatingCursor)
                            .or(liquor.averageRating.eq(avgRatingCursor).and(liquor.id.gt(idAfter)));
                }
            }
            default ->  {
                return null;
            }
        }
    }

    private Predicate categoryEq(Liquor.LiquorCategory category) {
        return category != null ? liquor.category.eq(category) : null;
    }

    private Predicate subcategoryEq(LiquorSubcategory subcategory) {
        return subcategory != null ? liquor.subcategory.eq(subcategory) : null;
    }

    private Predicate keywordContains(String keyword) {
        return keyword != null ? liquor.name.contains(keyword) : null;
    }

    private Predicate isDeletedEq(Boolean searchDeleted) {
        return searchDeleted != null ? liquor.isDeleted.eq(searchDeleted) : null;
    }

    /**
     * OrderSpecifiers
     */

    private OrderSpecifier<?> getOrderSpecifier(boolean descending, SortLiquorBy sortBy) {
        Order order = descending ? Order.DESC : Order.ASC;
        switch (sortBy) {
            case LIQUOR_ID -> {
                return new OrderSpecifier<>(order, liquor.id);
            }
            case LIKE_COUNT -> {
                return new OrderSpecifier<>(order, liquor.likeCount);
            }
            case AVERAGE_RATING -> {
                return new OrderSpecifier<>(order, liquor.averageRating);
            }
            default -> {
                return null;
            }
        }
    }

    private OrderSpecifier<?> getTieBreakerOrder(boolean descending) {
        Order order = descending ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, liquor.id);
    }
}
