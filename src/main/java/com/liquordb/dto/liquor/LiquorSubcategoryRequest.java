package com.liquordb.dto.liquor;

import com.liquordb.enums.LiquorCategory;
import jakarta.validation.constraints.NotBlank;

public record LiquorSubcategoryRequest(

        @NotBlank(message = "이름으로 공백은 사용할 수 없습니다.")
        String name,

        String description,

        LiquorCategory category
) {
}
