package com.liquordb.dto.liquor;

import com.liquordb.entity.LiquorTagId;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquorTagResponseDto {
    private LiquorTagId id;
    private Long liquorId;
    private Long tagId;
    private String tagName;
}
