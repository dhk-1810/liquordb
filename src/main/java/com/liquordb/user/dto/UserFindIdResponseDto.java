package com.liquordb.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ID찾기 응답 DTO입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFindIdResponseDto {
    private String email;
    private String nickname;
}
