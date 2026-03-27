package com.liquordb.repository.liquor;

import com.liquordb.dto.liquor.LiquorScoreDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.entity.Liquor;
import com.liquordb.repository.liquor.condition.LiquorSearchCondition;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CustomLiquorRepository {

    // 전체 조회
    Slice<Liquor> findAll(LiquorSearchCondition condition);

    // 인기 주류 조회
    List<LiquorSummaryDto> findTrendingLiquors(int limit);

    // 인기 순위 계산
    List<LiquorScoreDto> findScoresByIds(List<Long> id);
}
