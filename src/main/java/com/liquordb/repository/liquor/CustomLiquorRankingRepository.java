package com.liquordb.repository.liquor;

import com.liquordb.enums.PeriodType;

import java.util.List;

public interface CustomLiquorRankingRepository {

    List<Long> findTrendingLiquorIdsByPeriod(PeriodType period);
}
