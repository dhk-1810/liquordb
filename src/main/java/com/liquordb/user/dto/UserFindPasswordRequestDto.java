package com.liquordb.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 찾기 요청 DTO입니다.
 * 이메일로 임시 비밀번호가 발급됩니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFindPasswordRequestDto {
    private String email;
}
