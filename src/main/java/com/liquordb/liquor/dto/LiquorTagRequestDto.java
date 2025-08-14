package com.liquordb.liquor.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiquorTagRequestDto {
    private Long liquorId;
    private Long tagId;
}
