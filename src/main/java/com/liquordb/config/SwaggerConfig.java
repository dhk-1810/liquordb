package com.liquordb.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("Bearer Authentication")
                )
                .components(
                        new Components()
                                .addSecuritySchemes("Bearer Authentication", createTokenSecurityScheme())
                )
                .info(
                        new Info()
                                .title("LiquorDB REST API")
                                .description("주류 커뮤니티 서비스 API 명세서")
                                .version("v1.0.0")
                );

    }

    @Bean
    public OpenApiCustomizer springSecurityLoginCustomizer() {
        return openApi -> {
            // 1. 요청 바디(ID/PW) 정의
            Schema<?> loginSchema = new Schema<>()
                    .addProperty("username", new StringSchema().example("test@test.com"))
                    .addProperty("password", new StringSchema().example("Password1!"));

            RequestBody requestBody = new RequestBody()
                    .content(new Content()
                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                    new io.swagger.v3.oas.models.media.MediaType().schema(loginSchema)));

            // 2. 응답(JWT 정보) 정의
            ApiResponses responses = new ApiResponses()
                    .addApiResponse("200", new ApiResponse().description("로그인 성공 - 토큰 발급"))
                    .addApiResponse("401", new ApiResponse().description("인증 실패"));

            // 3. /api/auth/login 경로 생성
            PathItem loginPath = new PathItem().post(
                    new Operation()
                            .tags(Collections.singletonList("Auth"))
                            .summary("JSON 로그인")
                            .description("이메일과 비밀번호를 사용하여 JWT 토큰을 발급받습니다.")
                            .requestBody(requestBody)
                            .responses(responses)
            );

            // 4. 스웨거 문서에 경로 추가
            openApi.getPaths().addPathItem("/api/auth/login", loginPath);
        };
    }

    private SecurityScheme createTokenSecurityScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}
