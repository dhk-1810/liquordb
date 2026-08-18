package com.liquordb.dto.liquor;

import com.liquordb.entity.LiquorSubcategory;
import com.liquordb.enums.LiquorCategory;

public record LiquorSubcategoryResponse (
        Long id,
        String name,
        String nameKo,
        LiquorCategory category
) {
    public static LiquorSubcategoryResponse toDto(LiquorSubcategory subcategory) {
        return new LiquorSubcategoryResponse(
                subcategory.getId(),
                subcategory.getName(),
                subcategory.getNameKo(),
                subcategory.getCategory()
        );
    }
}
