package com.liquordb.exception;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.exception.notice.NoticeNotFoundException;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.exception.tag.LiquorTagAlreadyExistsException;
import com.liquordb.exception.tag.TagNotFoundException;
import com.liquordb.exception.tag.UserTagAlreadyExistsException;
import com.liquordb.exception.tag.UserTagNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            CommentNotFoundException.class,
            LiquorNotFoundException.class,
            NoticeNotFoundException.class,
            ReviewNotFoundException.class,
            UserNotFoundException.class,
            TagNotFoundException.class,
            UserTagNotFoundException.class,
    })
    public ResponseEntity<ErrorResponse> handleNotFound(LiquordbException e) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.NOT_FOUND,
                e.errorCode,
                e.getMessage(),
                e.details
        );
        log.warn("NotFoundException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({
            LiquorTagAlreadyExistsException.class,
            UserTagAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponse> handleAlreadyExists(LiquordbException e) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.CONFLICT,
                e.errorCode,
                e.getMessage(),
                e.details
        );
        log.warn("AlreadyExistsException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(LiquordbException.class)
    public ResponseEntity<ErrorResponse> handleLiquordbException(LiquordbException e) {
        ErrorResponse response = ErrorResponse.of(
                e.errorCode.getStatus(),
                e.errorCode,
                e.getMessage(),
                e.details
        );
        if (e.errorCode.getStatus().is5xxServerError()) {
            log.error(e.getMessage(), e);
        } else {
            log.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        }
        return ResponseEntity.status(e.errorCode.getStatus()).body(response);
    }

    // 타입 불일치 - PathVariable / RequestParam
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {

        String requiredType = (e.getRequiredType() != null)
                ? e.getRequiredType().getSimpleName()
                : "Unknown";

        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_INPUT_VALUE,
                e.getMessage(),
                Map.of(e.getName(), requiredType)
        );

        return ResponseEntity.badRequest().body(response);
    }

    // 타입 불일치 - JSON 본문 내부
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonError(HttpMessageNotReadableException e) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_INPUT_VALUE,
                e.getMessage(),
                Map.of()
        );
        return ResponseEntity.badRequest().body(response);
    }

    // Validation 실패
    @ExceptionHandler(MethodArgumentNotValidException .class)
    public ResponseEntity<ErrorResponse> handleValidationFailure(MethodArgumentNotValidException e) {

        List<String> sensitiveFields = List.of("password", "currentPassword", "newPassword");

        Map<String, Object> details = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> { // 에러 리스트를 돌면서 실패 정보를 취합
                            String fieldName = error.getField();
                            Object rejectedValue = error.getRejectedValue();

                            // 민감 필드인 경우 마스킹 처리
                            Object displayValue = sensitiveFields.contains(fieldName)
                                    ? "***"
                                    : (rejectedValue == null ? "" : rejectedValue);

                            return Map.of(
                                    "rejectedValue", displayValue,
                                    "message", Objects.requireNonNullElse(error.getDefaultMessage(), "유효하지 않은 입력값입니다.")
                            );
                        },
                        (existing, replacement) -> existing
                ));

        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst().map(FieldError::getDefaultMessage)
                .orElse("입력값 검증에 실패하였습니다.");

        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_INPUT_VALUE,
                message,
                details
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_INPUT_VALUE,
                e.getMessage(),
                Map.of()
        );
        log.warn("IllegalArgumentException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_STATE,
                e.getMessage(),
                Map.of()
        );
        log.warn("IllegalStateException: {}", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR,
                e.getMessage(),
                Map.of()
        );
        log.error("Exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
