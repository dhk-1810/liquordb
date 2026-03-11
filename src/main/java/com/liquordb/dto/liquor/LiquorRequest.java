package com.liquordb.dto.liquor;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
public record LiquorRequest(

        @NotBlank(message = "주류 이름은 필수입니다.")
        String name,

        @NotNull(message = "주류 분류는 필수입니다.")
        Liquor.LiquorCategory category,

        Long subcategoryId,

        @NotBlank(message = "주류 제조국 정보는 필수입니다.")
        String country,

        @NotBlank(message = "주류 제조사 정보는 필수입니다.")
        String manufacturer,

        @NotNull(message = "주류 도수 정보는 필수입니다.")
        Double abv,

        Boolean isDiscontinued // 단종 여부
) {

}
