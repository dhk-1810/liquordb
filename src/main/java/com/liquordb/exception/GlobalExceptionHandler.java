package com.liquordb.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                "해당 정보를 가진 사용자가 존재하지 않습니다."
        );
        log.error("UserNotFoundException: {}", ex.getMessage(), ex); // TODO 에러 형식 통일
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(LiquorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLiquorNotFound(LiquorNotFoundException ex) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.NOT_FOUND,
                "주류를 찾을 수 없습니다.",
                ex.getMessage()
        );
        log.error("LiquorNotFoundException: {}", ex.getMessage(), ex); // TODO 에러 형식 통일
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(ReviewNotFoundException ex) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.NOT_FOUND,
                "유저를 찾을 수 없습니다.",
                ex.getMessage()
        );
        log.error("ReviewNotFoundException: {}", ex.getMessage(), ex); // TODO 에러 형식 통일
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFound(CommentNotFoundException ex) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.NOT_FOUND,
                "댓글을 찾을 수 없습니다.",
                ex.getMessage()
        );
        log.error("CommentNotFoundException: {}", ex.getMessage(), ex); // TODO 에러 형식 통일
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}
