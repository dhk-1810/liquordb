package com.liquordb.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.ErrorResponse;
import com.liquordb.exception.user.BannedUserException;
import com.liquordb.exception.user.WithdrawnUserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ErrorCode errorCode = ErrorCode.LOGIN_FAILED;
        String errorMessage = "로그인에 실패했습니다.";

        // CustomUserDetailsService에서 던진 예외에 따라 분기
        if (exception instanceof WithdrawnUserException) {
            errorCode = ErrorCode.WITHDRAWN_USER;
            errorMessage = exception.getMessage();
        } else if (exception instanceof BannedUserException) {
            errorCode = ErrorCode.BANNED_USER;
            errorMessage = exception.getMessage();
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";
        }
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED, errorCode, errorMessage, null);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}