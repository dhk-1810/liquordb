package com.liquordb.dto.liquor;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquorTagResponseDto {
    private Long id;
    private Long liquorId;
    private Long tagId;
}
