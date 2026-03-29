package com.liquordb.repository.liquor;

import com.liquordb.enums.PeriodType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class LiquorRankingRepositoryImpl implements CustomLiquorRankingRepository{

    private final JPAQueryFactory queryFactory;
    private final QLiquorRanking liquorRanking = QLiquorRanking.liquorRanking;

    @Override
    public List<Long> findTrendingLiquorIdsByPeriod(PeriodType period) {
        return queryFactory
                .select(liquorRanking.liquorId)
                .from(liquorRanking)
                .where(liquorRanking.periodType.eq(period))
                .orderBy(liquorRanking.rankNumber.asc())
                .fetch();
    }
}
