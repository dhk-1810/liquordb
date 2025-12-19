package com.liquordb.enums;

public enum UserStatus {
    ACTIVE, // 기본
    SUSPENDED, // 리뷰, 댓글 기능 일시 이용제한
    BANNED, // 리뷰, 댓글 기능 영구 이용제한. = 강제 탈퇴, 해당 이메일로 재가입 불가.
    WITHDRAWN; // 회원 탈퇴, 재가입 가능.

    public boolean isActiveUser() {
        return (this == ACTIVE || this == SUSPENDED);
    }
}