package com.liquordb.repository.liquor;

import com.liquordb.entity.LiquorRanking;
import com.liquordb.enums.PeriodType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiquorRankingRepository extends JpaRepository<LiquorRanking, Long>, CustomLiquorRankingRepository {

    void deleteByPeriodType(PeriodType periodType);
}
