package com.liquordb.security;

import java.util.UUID;

/**
 * 리프레시 토큰, 로그아웃된 엑세스 토큰 저장
 * 동시 로그인 기기 대수 제한, 무효화된 엑세스 토큰 거부
 * 토큰 유효기간 만료 시 캐시에서 삭제
 */
public interface JwtRegistry {
    void registerRefreshToken(UUID userId, String refreshToken);
    void rotateRefreshToken(String oldRefreshToken, String newRefreshToken, UUID userId);
    void invalidateAllRefreshTokensByUserId(UUID userId);
    boolean isRefreshTokenActive(String refreshToken);

    void addToBlacklist(String accessToken, long remainingTtlMs);
    boolean isBlacklisted(String accessToken);
    void clearExpiredTokens(); //
}