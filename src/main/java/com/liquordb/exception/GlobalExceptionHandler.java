package com.liquordb.exception;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.exception.notice.NoticeNotFoundException;
import com.liquordb.exception.report.CommentReportAlreadyExistsException;
import com.liquordb.exception.report.CommentReportNotFoundException;
import com.liquordb.exception.report.ReviewReportAlreadyExistsException;
import com.liquordb.exception.report.ReviewReportNotFoundException;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

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
            CommentReportNotFoundException.class,
            ReviewReportNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(LiquordbException e) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.NOT_FOUND,
                e.errorCode,
                e.getMessage(),
                e.details
        );
        log.error("NotFoundException: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({
            CommentReportAlreadyExistsException.class,
            ReviewReportAlreadyExistsException.class,
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
        log.error("AlreadyExistsException: {}", e.getMessage(), e);
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
        log.error(e.getMessage(), e);
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

        Map<String, Object> details = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {    // 모든 에러 리스트를 돌면서 정보를 취합
            Map<String, Object> info = new HashMap<>();
            info.put("rejectedValue", error.getRejectedValue());
            info.put("message", error.getDefaultMessage()); // 직접 지정한 메시지

            details.put(error.getField(), info);
        });

        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage(); // 직접 지정한 메시지
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
        log.error("IllegalArgumentException: {}", e.getMessage(), e);
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
        log.error("IllegalStateException: {}", e.getMessage(), e);
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
