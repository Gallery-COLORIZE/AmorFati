package com.colorize.amorfati.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common (C001 ~ C099)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "허용되지 않은 HTTP 메소드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "대상을 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 내부 오류가 발생했습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C005", "요청한 리소스를 찾을 수 없습니다."),

    // Auth & Member (A001 ~ A099)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "A003", "존재하지 않는 회원입니다."),

    // Emotion Log (E001 ~ E099)
    EMOTION_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "해당 감정 기록을 찾을 수 없습니다."),
    INVALID_EMOTION_LEVEL(HttpStatus.BAD_REQUEST, "E002", "감정 레벨은 1~5 단계여야 합니다."),

    // Somatic Signal (S001 ~ S099)
    SOMATIC_SIGNAL_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "해당 신체 반응 신호를 찾을 수 없습니다."),

    // Trigger Factor (T001 ~ T099)
    TRIGGER_FACTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "해당 상황/트리거 요인을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
