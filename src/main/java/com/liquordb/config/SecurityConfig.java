package com.liquordb.config;

import com.liquordb.handler.JwtLoginSuccessHandler;
import com.liquordb.handler.JwtLogoutHandler;
import com.liquordb.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtLoginSuccessHandler jwtLoginSuccessHandler;
    private final JwtLogoutHandler jwtLogoutHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 기본 설정
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 인증 설정
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2 // TODO 점검
                        .loginPage("/login") // 소셜 로그인도 같은 로그인 페이지 사용
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                .logout(logout -> logout.addLogoutHandler(jwtLogoutHandler))

                // 인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "api/auth/signup").permitAll()
                        .requestMatchers("/api//users/*/reviews").authenticated()
                        .requestMatchers("/api/auth/token-refresh").permitAll()// TODO 로그인, 소셜로그인?
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
