package com.liquordb.handler;

import com.liquordb.repository.UserRepository;
import com.liquordb.security.JwtRegistry;
import com.liquordb.security.JwtTokenProvider;
import com.liquordb.security.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtRegistry jwtRegistry;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        // Access Token 추출, 무효화 (블랙리스트)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);

            long remainingMs = jwtTokenProvider.getRemainingExpiration(accessToken);
            if (remainingMs > 0) {
                jwtRegistry.addToBlacklist(accessToken, remainingMs);
            }
        }

        // Refresh Token 무효화
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            userRepository.findByEmail(userDetails.getUsername())
                    .ifPresent(user -> jwtRegistry.invalidateAllRefreshTokensByUserId(user.getId()));
        }

        // 쿠키 삭제 명령
        response.addCookie(TokenUtil.emptyRefreshTokenCookie());
    }
}
