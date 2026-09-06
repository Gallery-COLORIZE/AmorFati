package com.colorize.amorfati.global.error;

import com.colorize.amorfati.global.common.ApiResponse;
import com.colorize.amorfati.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("Business Exception: {} - {}", e.getErrorCode().getCode(), e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation Exception: {}", e.getMessage());
        List<ApiResponse.FieldErrorDetail> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiResponse.FieldErrorDetail(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE.getCode(), "입력값이 올바르지 않습니다.", fieldErrors));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        log.warn("Bind Exception: {}", e.getMessage());
        List<ApiResponse.FieldErrorDetail> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiResponse.FieldErrorDetail(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE.getCode(), "요청 파라미터가 올바르지 않습니다.", fieldErrors));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("Method Not Allowed: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(ErrorCode.METHOD_NOT_ALLOWED.getCode(), ErrorCode.METHOD_NOT_ALLOWED.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("Resource Not Found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND.getCode(), ErrorCode.RESOURCE_NOT_FOUND.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
