package com.liquordb.handler;

import com.liquordb.repository.UserRepository;
import com.liquordb.security.JwtRegistry;
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
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            userRepository.findByEmail(userDetails.getUsername())
                    .ifPresent(user -> jwtRegistry.invalidateAllRefreshTokensByUserId(user.getId()));
            userDetails.
        }

        // 쿠키 삭제 명령
        response.addCookie(TokenUtil.emptyRefreshTokenCookie());

        jwtRegistry.addToBlacklist(u);
    }
}
