package com.liquordb.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 클라이언트가 공지사항을 등록하거나 수정할 때 서버로 보내는 데이터를 담는 DTO입니다.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequestDto {
    private String title;
    private String content;
    private boolean isPinned;
}
