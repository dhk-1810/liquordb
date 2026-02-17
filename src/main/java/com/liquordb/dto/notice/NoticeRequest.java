package com.liquordb.dto.notice;

import jakarta.validation.constraints.NotBlank;

/**
 * 공지사항 등록, 수정 시 사용
 */
public record NoticeRequest(

        @NotBlank(message = "제목은 공백일 수 없습니다.")
        String title,

        @NotBlank(message = "내용은 공백일 수 없습니다.")
        String content
){

}
