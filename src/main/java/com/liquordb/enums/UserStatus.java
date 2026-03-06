package com.liquordb.enums;

public enum UserStatus {
    ACTIVE, // 기본
    WITHDRAWN; // 회원 탈퇴, 재가입 가능.

    public boolean isAvailable() {
        return (this == ACTIVE);
    }
}