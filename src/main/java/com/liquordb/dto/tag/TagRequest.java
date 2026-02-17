package com.liquordb.dto.tag;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public record TagRequest(

        @NotBlank(message = "태그 이름은 공백일 수 없습니다.")
        String name
){

}