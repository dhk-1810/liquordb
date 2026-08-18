package com.liquordb.dto.liquor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 JSON 결과에 포함하지 않음
public record LiquorDetailResponseDto(

        String type,
        
        // BEER
        String malts,
        String hops,
        Double ibu,
        
        // COCKTAIL
        String glass,
        String instructions,
        String ingredients,
        
        // WINE
        String grapeVariety,
        Integer vintage,
        String region,
        
        // WHISKY
        String caskType,
        Integer age
) {}
