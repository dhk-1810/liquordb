package com.liquordb.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Auth
    UNAUTHENTICATED_USER (HttpStatus.UNAUTHORIZED), // 인증 정보 충족 실패,
    ACCESS_DENIED (HttpStatus.FORBIDDEN), // 인가 정보 충족 실패,
    LOGIN_FAILED (HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD (HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN (HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN (HttpStatus.UNAUTHORIZED),

    // User
    USER_ACCESS_DENIED (HttpStatus.FORBIDDEN),
    USER_NOT_FOUND (HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS (HttpStatus.CONFLICT),
    DUPLICATE_USERNAME (HttpStatus.CONFLICT),
    DUPLICATE_EMAIL (HttpStatus.CONFLICT),
    SUSPENDED_USER (HttpStatus.FORBIDDEN), // 활동제한
    BANNED_USER (HttpStatus.FORBIDDEN), // 강제탈퇴
    WITHDRAWN_USER (HttpStatus.CONFLICT), // 자발적 탈퇴 (회원가입 시 기존 정보 존재)

    // Liquor
    LIQUOR_NOT_FOUND (HttpStatus.NOT_FOUND),
    LIQUOR_ALREADY_EXISTS (HttpStatus.CONFLICT),

    // Review
    REVIEW_ACCESS_DENIED (HttpStatus.FORBIDDEN),
    REVIEW_NOT_FOUND (HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_EXISTS (HttpStatus.CONFLICT),

    // Comment
    COMMENT_ACCESS_DENIED (HttpStatus.FORBIDDEN),
    COMMENT_NOT_FOUND (HttpStatus.NOT_FOUND),
    INVALID_PARENT_COMMENT (HttpStatus.CONFLICT),

    // Tag
    TAG_NOT_FOUND (HttpStatus.NOT_FOUND),
    TAG_ALREADY_EXISTS (HttpStatus.CONFLICT),
    USER_TAG_NOT_FOUND (HttpStatus.NOT_FOUND),
    USER_TAG_ALREADY_EXISTS (HttpStatus.CONFLICT),
    LIQUID_NOT_FOUND (HttpStatus.NOT_FOUND),
    LIQUOR_TAG_ALREADY_EXISTS (HttpStatus.CONFLICT),

    // Report
    COMMENT_REPORT_NOT_FOUND (HttpStatus.NOT_FOUND),
    COMMENT_REPORT_ALREADY_EXISTS (HttpStatus.CONFLICT),
    REVIEW_REPORT_NOT_FOUND (HttpStatus.NOT_FOUND),
    REVIEW_REPORT_ALREADY_EXISTS (HttpStatus.CONFLICT),

    // Notice
    NOTICE_NOT_FOUND (HttpStatus.NOT_FOUND),

    // File
    BINARY_CONTENT_NOT_FOUND (HttpStatus.NOT_FOUND),
    DUPLICATE_BINARY_CONTENT (HttpStatus.CONFLICT),
    BINARY_CONTENT_UPLOAD_ALREADY_SUCCEEDED (HttpStatus.CONFLICT),
    INVALID_FILE_FORMAT (HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED (HttpStatus.PAYLOAD_TOO_LARGE),

    // Notification
    NOTIFICATION_NOT_FOUND (HttpStatus.NOT_FOUND),

    // etc
    INVALID_INPUT_VALUE (HttpStatus.BAD_REQUEST),
    INVALID_STATE (HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED (HttpStatus.METHOD_NOT_ALLOWED),
    LOCK_ACQUISITION_FAILED (HttpStatus.CONFLICT),
    INTERNAL_SERVER_ERROR (HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() { return status; }
}
