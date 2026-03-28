package com.liquordb.repository.liquor;

import com.liquordb.dto.liquor.LiquorScoreDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.entity.*;
import com.liquordb.enums.LiquorCategory;
import com.liquordb.enums.SortLiquorBy;
import com.liquordb.repository.liquor.condition.LiquorSearchCondition;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

import static com.querydsl.jpa.JPAExpressions.select;

@RequiredArgsConstructor
@Repository
@Slf4j
public class LiquorRepositoryImpl implements CustomLiquorRepository{

    private final JPAQueryFactory queryFactory;
    private final QLiquor liquor = QLiquor.liquor;

    @Override
    public Slice<Liquor> findAll(LiquorSearchCondition condition) {

        int limit = condition.limit();
        List<Liquor> content = queryFactory.selectFrom(liquor)
                .where(
                        categoryEq(condition.category()),
                        subcategoryIdEq(condition.subcategoryId()),
                        keywordContains(condition.keyword()),
                        isDeletedEq(condition.searchDeleted()),
                        tagsAllMatch(condition.tagIds()),
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

    @Override
    public List<LiquorSummaryDto> findTrendingLiquorSummaries(List<Long> ids, int limit) {

    }

    @Override
    public List<LiquorScoreDto> findScoresByIds(List<Long> id) {
        return queryFactory.selectFrom(liquor)
                .where()
                .orderBy()
                .limit()
                .fetch()
                ;
    }

    /**
     * Predicates
     */

    private Predicate cursorCondition(String cursor, Long idAfter, SortLiquorBy sortBy, boolean descending) {
        if (!StringUtils.hasText(cursor)) {
            return null; // 첫 페이지 조회
        }
        // 주 커서
        try {
            return switch (sortBy) {
                case LIQUOR_ID -> {
                    Long idCursor = Long.parseLong(cursor);
                    yield descending ? liquor.id.lt(idCursor) : liquor.id.gt(idCursor);
                }
                case LIKE_COUNT -> {
                    Long likeCountCursor = Long.parseLong(cursor);
                    yield descending
                            ? liquor.likeCount.lt(likeCountCursor)
                            .or(liquor.likeCount.eq(likeCountCursor).and(liquor.id.lt(idAfter)))
                            : liquor.likeCount.gt(likeCountCursor)
                            .or(liquor.likeCount.eq(likeCountCursor).and(liquor.id.gt(idAfter)));
                }
                case AVERAGE_RATING -> {
                    Double avgRatingCursor = Double.parseDouble(cursor);
                    yield descending
                            ? liquor.averageRating.lt(avgRatingCursor)
                            .or(liquor.averageRating.eq(avgRatingCursor).and(liquor.id.lt(idAfter)))
                            : liquor.averageRating.gt(avgRatingCursor)
                            .or(liquor.averageRating.eq(avgRatingCursor).and(liquor.id.gt(idAfter)));

                }
                default -> null;
            };
        } catch (NumberFormatException e) {
            log.warn("잘못된 형식의 커서 값이 입력되었습니다: {}", cursor);
            return null;
        }

    }

    private Predicate categoryEq(LiquorCategory category) {
        return category != null ? liquor.category.eq(category) : null;
    }

    private Predicate subcategoryIdEq(Long subcategoryId) {
        return subcategoryId != null ? liquor.subcategoryId.eq(subcategoryId) : null;
    }

    private Predicate keywordContains(String keyword) {
        return keyword != null ? liquor.name.contains(keyword) : null;
    }

    private Predicate isDeletedEq(Boolean searchDeleted) {
        return searchDeleted != null ? liquor.isDeleted.eq(searchDeleted) : null;
    }

    private Predicate tagsAllMatch(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return null;
        }
        QLiquorTag subLiquorTag = new QLiquorTag("subLiquorTag"); // 서브쿼리의 LiquorTag는 메인쿼리와 분리 필요.

        return liquor.id.in(
                select(subLiquorTag.liquor.id)
                        .from(subLiquorTag)
                        .where(subLiquorTag.tag.id.in(tagIds))
                        .groupBy(subLiquorTag.liquor.id)
                        // 그룹화된 liquor_id 중 태그 개수 == 선택한 개수인 것만 필터링
                        .having(subLiquorTag.liquor.id.count().eq((long) tagIds.size()))
        );
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
