package com.liquordb.user.entity;

public enum UserStatus {
    ACTIVE, // 기본
    WARNED, // 경고만
    RESTRICTED, // 리뷰, 댓글 기능 일시 이용제한
    BANNED, //리뷰, 댓글 기능 영구 이용제한. = 강제 탈퇴, 재가입 불가.
    WITHDRAWN // 회원 탈퇴 (Soft Delete), 재가입 가능.
}