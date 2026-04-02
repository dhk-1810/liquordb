package com.liquordb.repository.liquor;

import com.liquordb.dto.liquor.LiquorScoreDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.entity.Liquor;
import com.liquordb.repository.liquor.condition.LiquorSearchCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Set;

public interface CustomLiquorRepository {

    // 전체 조회
    Slice<Liquor> findAll(LiquorSearchCondition condition);

    // 인기 순위 계산 - scheduler
    List<LiquorScoreDto> findScoresByIds(Set<Long> activeIds);
}
