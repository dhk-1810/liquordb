package com.liquordb.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liquordb.dto.auth.AuthResponseDto;
import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.entity.User;
import com.liquordb.enums.UserStatus;
import com.liquordb.mapper.UserMapper;
import com.liquordb.repository.user.UserRepository;
import com.liquordb.service.S3Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
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
    private final UserRepository userRepository;
    private final S3Service s3Service;
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

        User user = userRepository.findById(userDetails.id())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userDetails.id()));
        UserResponseDto userDto = UserMapper.toDto(user, s3Service.getProfileImageUrl(user.getProfileImageKey()));

        String accessToken;
        if (userDetails.status() == UserStatus.WITHDRAWN) {
            // 탈퇴 계정은 복구를 위해 5분 유효기간의 임시 AccessToken만 발급 (RefreshToken 생성 및 저장 안 함)
            accessToken = jwtTokenProvider.createCustomAccessToken(
                    userDetails.id(),
                    userDetails.email(),
                    userDetails.role().name(),
                    TEMP_ACCESS_TOKEN_LIFETIME
            );
        } else {
            // 정상 계정인 경우 정상 토큰 및 RefreshToken 쿠키 발행
            accessToken = jwtTokenProvider.createAccessToken(
                    userDetails.id(),
                    userDetails.email(),
                    userDetails.role().name()
            );
            String refreshToken = jwtTokenProvider.createRefreshToken(
                    userDetails.id(),
                    userDetails.email(),
                    userDetails.role().name()
            );

            jwtRegistry.registerRefreshToken(userDetails.id(), refreshToken);

            Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
            response.addCookie(refreshCookie);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        AuthResponseDto authResponseDto = new AuthResponseDto(userDto, accessToken);

        response.getWriter().write(objectMapper.writeValueAsString(authResponseDto));
    }
}
