package com.liquordb.dto.tag;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagRequestDto {
    private String name;
}