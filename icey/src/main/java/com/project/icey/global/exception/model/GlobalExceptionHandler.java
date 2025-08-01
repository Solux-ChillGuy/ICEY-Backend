package com.project.icey.global.exception.model;


import com.project.icey.global.dto.ApiResponseTemplete;
import com.project.icey.global.exception.AlreadyJoinedException;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseTemplete<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        // 예외 메시지에서 "Unknown status"를 포함하고 있는 경우 처리
        if (e.getMessage().contains("Unknown status")) {
            // 특정 'Unknown status' 메시지를 사용자 정의 에러로 처리
            return ApiResponseTemplete.error(ErrorCode.INVALID_ENUM_VALUE, "Invalid status value provided: " + extractInvalidStatus(e));
        }
        // 다른 HttpMessageNotReadableException 처리
        return ApiResponseTemplete.error(ErrorCode.INVALID_REQUEST, "Malformed request: " + e.getMessage());
    }

    // 예외 메시지에서 잘못된 Status 값만 추출하는 유틸리티 메서드
    private String extractInvalidStatus(HttpMessageNotReadableException e) {
        String message = e.getMessage();
        // "Unknown status: ddd" 형식의 메시지를 처리
        if (message.contains("Unknown status")) {
            return message.split(":")[1].trim(); // "ddd"
        }
        return "Unknown status";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseTemplete<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        // 기타 IllegalArgumentException 처리 (예: 리소스가 없을 경우)
        return ApiResponseTemplete.error(ErrorCode.RESOURCE_NOT_FOUND, "Resource not found: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseTemplete<String>> handleGeneralException(Exception e) {
        // 기타 예외 처리
        return ApiResponseTemplete.error(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error occurred: " + e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponseTemplete<String>> handleMissingParamException(MissingServletRequestParameterException e) {
        return ApiResponseTemplete.error(ErrorCode.LOGIN_USER_FAILED,  e.getMessage() );

    }

    @ExceptionHandler(CoreApiException.class)
    public ResponseEntity<ApiResponseTemplete<String>> handleCoreApiException(CoreApiException e) {
        return ApiResponseTemplete.error(e.getErrorCode(), e.getMessage()); //여기를 LLM_SERVER_ERROR에서 상태코드 가지고 오는걸로 수정했습니다.. 이래도 될까요..?
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponseTemplete<String>> handleResponseStatusException(ResponseStatusException e) {
        HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        ErrorCode errorCode = mapHttpStatusToErrorCode(status);

        return ApiResponseTemplete.error(errorCode, e.getReason());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponseTemplete<String>> handleInvalidTokenException(InvalidTokenException e) {
        return ApiResponseTemplete.error(ErrorCode.UNAUTHORIZED_EXCEPTION, e.getMessage());
    }

    @ExceptionHandler(AlreadyJoinedException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyJoined(AlreadyJoinedException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("success", false);
        body.put("message", e.getMessage());
        // teamId를 같이 넣기
        body.put("teamId", e.getTeamId());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }


    private ErrorCode mapHttpStatusToErrorCode(HttpStatus status) {
        switch (status) {
            case BAD_REQUEST:
                return ErrorCode.INVALID_REQUEST;
            case UNAUTHORIZED:
                return ErrorCode.UNAUTHORIZED_EXCEPTION;
            case FORBIDDEN:
                return ErrorCode.ACCESS_DENIED_EXCEPTION;
            case NOT_FOUND:
                return ErrorCode.RESOURCE_NOT_FOUND;
            case CONFLICT:
                return ErrorCode.ALREADY_EXIST_SUBJECT_EXCEPTION;
            case INTERNAL_SERVER_ERROR:
                return ErrorCode.INTERNAL_SERVER_ERROR;
            default:
                return ErrorCode.UNKNOWN_ERROR;
        }

    }

}