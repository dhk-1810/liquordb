package com.liquordb.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ID찾기 요청 DTO입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFindIdRequestDto {
    private String email; // 이메일로 찾기
}