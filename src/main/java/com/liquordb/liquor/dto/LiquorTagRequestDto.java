package com.liquordb.liquor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquorTagRequestDto {
    private Long liquorId;
    private Long tagId;
}
