package com.liquordb.liquor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquorTagResponseDto {
    private Long id;
    private Long liquorId;
    private Long tagId;
}
