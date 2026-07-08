package com.liquordb.dto.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // 값 없는 필드는 JSON에서 누락
public record ReviewDetailResponseDto(
        String type,
        Double aroma,
        Double taste,
        Double headRetention,
        Double look,
        Double sweetness,
        Double acidity,
        Double body,
        Double tannin,
        Double finish
) {}
