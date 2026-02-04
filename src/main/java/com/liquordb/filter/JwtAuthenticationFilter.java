package com.liquordb.filter;

import com.liquordb.security.JwtRegistry;
import com.liquordb.security.JwtTokenProvider;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 인증 수행 - 엑세스 토큰 검증
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserDetailsService userDetailsService;
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        // 엑세스 토큰 서명 유효성, 만료 여부 검증
        if (StringUtils.hasText(token) && jwtTokenProvider.validateAccessToken(token)) {
            JWTClaimsSet claims = jwtTokenProvider.getClaims(token);
            if (claims == null) {
                log.error("토큰 정보를 읽을 수 없습니다.");
                filterChain.doFilter(request, response); // 인증되지 않은 익명 사용자로 처리
                return;
            }
            String username = claims.getSubject();
            String role = (String) claims.getClaim("role");

            if (StringUtils.hasText(username) && role != null) {
                if (isTokenValidInRegistry(token)) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("사용자 인증 완료: {}", username);
                } else {
                    log.warn("토큰이 유효하지 않습니다.");
                }

            } else {
                log.warn("토큰 내에 필수 사용자 정보가 누락되었습니다. (subject: {}, role: {})", username, role);
            }
        }
        filterChain.doFilter(request, response);

    }

    // 토큰 추출 - 요청 헤더에 Bearer로 시작하는 토큰 문자열을 꺼냄.
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length()); // 접두사 "Bearer "를 떼내고 JWT 문자열만 추출
        }
        return null;
    }

    // 토큰 상태 검증
    private boolean isTokenValidInRegistry(String token) {
        if (jwtRegistry.hasActiveJwtInformationByAccessToken(token)) { // 서비스 로직에 맞는 메서드 호출
            return true;
        } else {
            log.warn("유효하지 않은 토큰입니다.");
            return false;
        }
    }
}
