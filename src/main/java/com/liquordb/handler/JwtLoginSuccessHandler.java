package com.liquordb.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liquordb.dto.JwtDto;
import com.liquordb.entity.User;
import com.liquordb.mapper.UserMapper;
import com.liquordb.repository.UserRepository;
import com.liquordb.security.JwtInformation;
import com.liquordb.security.JwtProperties;
import com.liquordb.security.JwtTokenProvider;
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

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider; // 토큰 생성
    private final JwtProperties jwtProperties;
    private final JwtRegistry jwtRegistry; // 중복 로그인 체크
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper; // JSON 변환
    private final UserMapper userMapper; // 객체 변환
    private static final int REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24 * 14;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 사용자 정보 추출
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user =  userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        // 토큰 발행, 레지스트리 등록
        String accessToken = jwtTokenProvider.generateToken(
                user.getUsername(),
                user.getRole().name(),
                jwtProperties.getAccessTokenValidityInMs()
        );
        String refreshToken = jwtTokenProvider.generateToken(
                user.getUsername(),
                user.getRole().name(),
                jwtProperties.getRefreshTokenValidityInMs()
        );

        JwtInformation jwtInformation = new JwtInformation(
                userMapper.toDto(user),
                accessToken,
                refreshToken
        );
        jwtRegistry.registerJwtInformation(jwtInformation); // 동시 로그인 제한 처리도 수행됨.

        // 토큰 전달 - 엑세스 토큰은 JSON 바디로, 리프레시 토큰은 쿠키로.
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
        response.addCookie(refreshCookie);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        JwtDto jwtDto = JwtDto.builder()
                .userDto(userMapper.toDto(user))
                .accessToken(accessToken)
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
    }

}
