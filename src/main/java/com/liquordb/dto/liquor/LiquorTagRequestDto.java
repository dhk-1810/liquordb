package com.liquordb.dto.liquor;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiquorTagRequestDto {
    private Long liquorId;
    private Long tagId;
}
