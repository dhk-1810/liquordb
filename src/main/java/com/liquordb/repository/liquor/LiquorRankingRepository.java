package com.liquordb.repository.liquor;

import com.liquordb.entity.LiquorRanking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiquorRankingRepository extends JpaRepository<LiquorRanking, Long>, CustomLiquorRankingRepository {
}
