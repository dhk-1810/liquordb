package com.liquordb.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liquordb.dto.JwtDto;
import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.entity.User;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 사용자 정보 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserResponseDto userDto = userDetails.dto();

        // 토큰 발행, 레지스트리 등록
        String accessToken = jwtTokenProvider.createAccessToken(
                userDto.username(),
                userDto.role().name()
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(
                userDto.username(),
                userDto.role().name()
        );

        jwtRegistry.registerRefreshToken(userDto.id(), refreshToken); // 동시 로그인 제한 처리도 수행됨.

        // 토큰 전달 - 엑세스 토큰은 JSON 바디로, 리프레시 토큰은 쿠키로.
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
        response.addCookie(refreshCookie);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        JwtDto jwtDto = new JwtDto(userDto, accessToken);

        response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
    }
}
