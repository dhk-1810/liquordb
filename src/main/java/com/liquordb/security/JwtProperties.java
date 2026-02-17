package com.liquordb.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 관련된 설정값들 관리.
 * application.yml의 값을 객체로 매핑.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret; // 비밀키
    private String issuer; // 발행자
    private long accessTokenValidityInMs;
    private long refreshTokenValidityInMs;
}

