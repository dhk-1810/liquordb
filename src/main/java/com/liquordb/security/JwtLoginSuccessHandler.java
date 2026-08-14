package com.liquordb.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liquordb.dto.JwtDto;
import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.entity.User;
import com.liquordb.enums.UserStatus;
import com.liquordb.mapper.UserMapper;
import com.liquordb.repository.user.UserRepository;
import com.liquordb.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.liquordb.security.TokenUtil.REFRESH_TOKEN_MAX_AGE;

@RequiredArgsConstructor
@Component
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final ObjectMapper objectMapper; // JSON 변환
    private static final long TEMP_ACCESS_TOKEN_LIFETIME = 5 * 60 * 1000L;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        // 사용자 정보 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserResponseDto userDto = userDetails.dto();

        String accessToken;
        if (userDto.status() == UserStatus.WITHDRAWN) {
            // 탈퇴 계정은 복구를 위해 5분 유효기간의 임시 AccessToken만 발급 (RefreshToken 생성 및 저장 안 함)

            accessToken = jwtTokenProvider.createCustomAccessToken(
                    userDto.email(),
                    userDto.role().name(),
                    TEMP_ACCESS_TOKEN_LIFETIME
            );
        } else {
            // 정상 계정인 경우 정상 토큰 및 RefreshToken 쿠키 발행
            accessToken = jwtTokenProvider.createAccessToken(
                    userDto.email(),
                    userDto.role().name()
            );
            String refreshToken = jwtTokenProvider.createRefreshToken(
                    userDto.email(),
                    userDto.role().name()
            );

            jwtRegistry.registerRefreshToken(userDto.id(), refreshToken);

            Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
            response.addCookie(refreshCookie);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        JwtDto jwtDto = new JwtDto(userDto, accessToken);

        response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
    }
}
