package com.liquordb.dto.liquor;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
public record LiquorRequest(

        @NotBlank String name,
        @NotNull Liquor.LiquorCategory category,
        @NotNull LiquorSubcategory subcategory,
        @NotBlank String country,
        @NotBlank String manufacturer,
        @NotNull Double abv,
        @NotNull Boolean isDiscontinued, // 단종 여부
        String imageUrl
) {

}
