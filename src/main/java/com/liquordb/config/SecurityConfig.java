package com.liquordb.config;

import com.liquordb.filter.JsonUsernamePasswordAuthenticationFilter;
import com.liquordb.filter.JwtAuthenticationFilter;
import com.liquordb.handler.JwtLogoutHandler;
import com.liquordb.security.JwtLoginSuccessHandler;
import com.liquordb.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtLoginSuccessHandler jwtLoginSuccessHandler;
    private final JwtLogoutHandler jwtLogoutHandler;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final CustomOAuth2UserService customOAuth2UserService;

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
//                .oauth2Login(oauth2 -> oauth2 // TODO 점검
//                        .loginPage("/login") // 소셜 로그인도 같은 로그인 페이지 사용
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(customOAuth2UserService)
//                        )
//                )
                .logout(logout -> logout.addLogoutHandler(jwtLogoutHandler))

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/", "/api/auth/*").permitAll()
                        .requestMatchers("/api/auth/token-refresh").permitAll() // TODO 소셜로그인?
                        .requestMatchers(HttpMethod.GET, "/api/liquors/**").permitAll() // 주류, 리뷰 조회 허용
                        .requestMatchers(HttpMethod.GET, "/api/reviews/*/comments/**").permitAll() // 댓글 조회 허용
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonLoginFilter() throws Exception {
        JsonUsernamePasswordAuthenticationFilter filter =
                new JsonUsernamePasswordAuthenticationFilter(authenticationConfiguration.getAuthenticationManager());

        filter.setFilterProcessesUrl("/api/auth/login");
        filter.setAuthenticationSuccessHandler(jwtLoginSuccessHandler);
        return filter;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("ADMIN").implies("USER")
                .build();
    }
}
