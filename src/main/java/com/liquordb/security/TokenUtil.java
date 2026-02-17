package com.liquordb.security;

import jakarta.servlet.http.Cookie;

/**
 * 클라이언트에게 토큰을 쿠키에 저장하도록 지시
 */
public class TokenUtil {

    private static final int REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24 * 14;

    // 리프레시 토큰을 쿠키에 저장 지시. 엑세스 토큰은 클라이언트측 메모리의 변수로 저장
    public static Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true); // JS에서 접근 불가
        refreshCookie.setPath("/");      // 모든 경로에서 전송
        refreshCookie.setMaxAge(REFRESH_TOKEN_MAX_AGE); // 2주
        // refreshCookie.setSecure(true); // HTTPS 적용 시 주석 해제
        return refreshCookie;
    }

    public static Cookie emptyRefreshTokenCookie() {
        Cookie cookie = new Cookie("REFRESH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 만료 시간을 0으로 설정 -> 즉시 삭제
        // cookie.setSecure(true); // HTTPS 적용 시 주석 해제
        return cookie;
    }
}