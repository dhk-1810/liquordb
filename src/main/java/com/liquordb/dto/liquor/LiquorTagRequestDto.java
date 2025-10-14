package com.liquordb.dto.liquor;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquorTagRequestDto {
    private Long liquorId;
    private Long tagId;
}
