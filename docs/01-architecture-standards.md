# [Architecture Standards] 시스템 아키텍처 및 백엔드 개발 표준

| 항목 | 내용 |
| :--- | :--- |
| **문서 버전** | v1.1 |
| **작성일** | 2026-08-28 |
| **기반 기술** | Java 25, Spring Boot 4.1.1, Spring Data JPA, Spring Security, Thymeleaf |
| **문서 목적** | AmorFati 백엔드 아키텍처, 패키지 구조, DTO 패턴, 공통 규격 및 코딩 컨벤션 확립 |

---

## 1. 개요 및 설계 철학

AmorFati는 1인 개발 및 향후 확장을 고려하여 **도메인 주도형(Feature/Domain-centric) 패키지 구조**와 **일관된 공통 응답/예외/DTO 체계**를 지향합니다.
- **인지 부하 최소화**: 단순하고 예측 가능한 레이어링 구조를 유지합니다.
- **모던 Java 25 기능 적극 활용**: 불변 `record` 및 Compact Constructor를 통한 견고한 DTO 설계.
- **REST API와 SSR의 명확한 분리**: 화면 렌더링(Thymeleaf)과 데이터 처리(REST API)의 책임을 명확히 구분합니다.
- **안정적인 데이터 모델링**: 불변성을 최대한 보장하고, JPA N+1 문제 및 OSIV 부하를 사전에 방지합니다.

---

## 2. 패키지 구조 (Package Architecture)

`com.colorize.amorfati` 패키지 하위를 **도메인(`domain`)**, **웹 화면(`web`)**, **글로벌 공통(`global`)** 3개 영역으로 분리합니다.

```text
com.colorize.amorfati
├── AmorFatiApplication.java
│
├── domain/                      # 핵심 비즈니스 도메인 (도메인별 응집)
│   ├── emotion/                 # 감정 기록 도메인
│   │   ├── controller/          # REST API (@RestController)
│   │   │   └── EmotionApiController.java
│   │   ├── dto/                 # Outer Class + Inner Record 그룹화 DTO
│   │   │   ├── EmotionRequest.java   # Create, Update
│   │   │   └── EmotionResponse.java  # Detail, TimelineItem
│   │   ├── entity/              # JPA 엔티티 및 Enum
│   │   │   ├── EmotionLog.java
│   │   │   ├── EmotionLogSomatic.java
│   │   │   ├── EmotionLogTrigger.java
│   │   │   └── EmotionLevel.java
│   │   ├── repository/          # Spring Data JPA Repository
│   │   │   ├── EmotionLogRepository.java
│   │   │   ├── EmotionLogSomaticRepository.java
│   │   │   └── EmotionLogTriggerRepository.java
│   │   └── service/             # 비즈니스 로직
│   │       └── EmotionService.java
│   │
│   ├── somatic/                 # 신체 반응 신호 도메인 (9종 프리셋)
│   │   ├── controller/
│   │   │   └── SomaticSignalApiController.java
│   │   ├── dto/
│   │   │   └── SomaticSignalResponse.java
│   │   ├── entity/
│   │   │   └── SomaticSignal.java
│   │   ├── repository/
│   │   │   └── SomaticSignalRepository.java
│   │   └── service/
│   │       └── SomaticSignalService.java
│   │
│   ├── trigger/                 # 상황 및 트리거 도메인 (8종 프리셋)
│   │   ├── controller/
│   │   │   └── TriggerFactorApiController.java
│   │   ├── dto/
│   │   │   └── TriggerFactorResponse.java
│   │   ├── entity/
│   │   │   └── TriggerFactor.java
│   │   ├── repository/
│   │   │   └── TriggerFactorRepository.java
│   │   └── service/
│   │       └── TriggerFactorService.java
│   │
│   └── member/                  # 사용자 계정 도메인
│       ├── entity/
│       │   └── Member.java
│       ├── repository/
│       │   └── MemberRepository.java
│       └── service/
│           └── MemberService.java
│
├── web/                         # Thymeleaf 화면 렌더링 컨트롤러
│   ├── HomeController.java      # 메인 대시보드 / 감정 기록 페이지 라우팅
│   └── EmotionViewController.java
│
└── global/                      # 전역 공통 설정 및 인프라
    ├── auth/                    # 인증 및 Security 컨텍스트 헬퍼
    │   ├── CustomUserDetails.java
    │   └── CurrentUser.java     # @CurrentUser 커스텀 애노테이션
    ├── common/                  # 공통 응답, 엔티티, 유틸
    │   ├── ApiResponse.java     # 공통 REST 응답 래퍼
    │   ├── BaseTimeEntity.java  # 생성일/수정일 Auditing
    │   └── ResponseCode.java    # 상태 코드 인터페이스
    ├── config/                  # 스프링 설정
    │   ├── JpaAuditingConfig.java
    │   ├── SecurityConfig.java
    │   └── WebMvcConfig.java
    └── error/                   # 전역 예외 처리
        ├── ErrorCode.java       # 비즈니스 에러 코드 Enum
        ├── GlobalExceptionHandler.java  # @RestControllerAdvice
        └── exception/
            ├── BusinessException.java   # 최상위 비즈니스 예외
            ├── EntityNotFoundException.java
            └── InvalidInputException.java
```

---

## 3. 모던 DTO 설계 표준 (Modern Record DTO Pattern)

AmorFati는 Java 25의 언어적 장점을 극대화하기 위해 **`Outer Class + Static Inner Record`** 패턴을 표준으로 채택합니다.

### 3.1 DTO 3대 작성 규칙
1. **불변성 보장**: 모든 DTO는 Java `record`로 작성하여 완벽한 불변(Immutable) 상태를 유지합니다.
2. **도메인 단위 네임스페이스 그룹화**: 요청은 `[Domain]Request`, 응답은 `[Domain]Response` 래퍼 클래스 내부의 static record로 정의합니다. (파일 개수 폭발 방지 및 맥락 명확화)
3. **단방향 매핑 책임 분리**:
   - **Request DTO**: `toEntity(...)` 메서드로 엔티티 생성을 캡슐화합니다.
   - **Response DTO**: `from(entity, ...)` 정적 팩토리 메서드로 엔티티로부터 변환을 수행합니다.
   - *규칙: Entity는 DTO의 존재를 몰라야 하며, DTO만 Entity를 참조합니다.*

---

### 3.2 요청 DTO 명세 예시 (`EmotionRequest.java`)

Compact Constructor를 활용하여 **Null 방어(`List.of()`)** 및 **기본값(`LocalDateTime.now()`)**을 캡슐화합니다.

```java
package com.colorize.amorfati.domain.emotion.dto;

import com.colorize.amorfati.domain.emotion.entity.EmotionLevel;
import com.colorize.amorfati.domain.emotion.entity.EmotionLog;
import com.colorize.amorfati.domain.member.entity.Member;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class EmotionRequest {

    private EmotionRequest() {} // 래퍼 클래스 인스턴스화 방지

    // 1. 감정 생성 요청 (1초 원터치 퀵 체크)
    public record Create(
            @NotNull(message = "감정 레벨은 필수입니다.")
            @Min(value = 1, message = "감정 레벨은 1 이상이어야 합니다.")
            @Max(value = 5, message = "감정 레벨은 5 이하여야 합니다.")
            Integer level,

            List<Long> somaticSignalIds,
            List<Long> triggerFactorIds,

            @Size(max = 1000, message = "메모는 최대 1000자까지 작성할 수 있습니다.")
            String memo,

            LocalDateTime recordedAt
    ) {
        // Compact Constructor: null 방어 및 기본값 세팅
        public Create {
            somaticSignalIds = (somaticSignalIds == null) ? List.of() : List.copyOf(somaticSignalIds);
            triggerFactorIds = (triggerFactorIds == null) ? List.of() : List.copyOf(triggerFactorIds);
            recordedAt = (recordedAt == null) ? LocalDateTime.now() : recordedAt;
        }

        public EmotionLog toEntity(Member member, EmotionLevel emotionLevel) {
            return EmotionLog.builder()
                    .member(member)
                    .emotionLevel(emotionLevel)
                    .memo(memo)
                    .recordedAt(recordedAt)
                    .build();
        }
    }

    // 2. 감정 수정 요청
    public record Update(
            @Min(1) @Max(5)
            Integer level,
            List<Long> somaticSignalIds,
            List<Long> triggerFactorIds,
            @Size(max = 1000)
            String memo
    ) {
        public Update {
            somaticSignalIds = (somaticSignalIds == null) ? List.of() : List.copyOf(somaticSignalIds);
            triggerFactorIds = (triggerFactorIds == null) ? List.of() : List.copyOf(triggerFactorIds);
        }
    }
}
```

---

### 3.3 응답 DTO 명세 예시 (`EmotionResponse.java`)

```java
package com.colorize.amorfati.domain.emotion.dto;

import com.colorize.amorfati.domain.emotion.entity.EmotionLevel;
import com.colorize.amorfati.domain.emotion.entity.EmotionLog;
import com.colorize.amorfati.domain.somatic.dto.SomaticSignalResponse;
import com.colorize.amorfati.domain.trigger.dto.TriggerFactorResponse;

import java.time.LocalDateTime;
import java.util.List;

public class EmotionResponse {

    private EmotionResponse() {}

    // 1. 단건 상세 응답
    public record Detail(
            Long id,
            EmotionLevel level,
            int levelScore,
            String memo,
            List<SomaticSignalResponse> somaticSignals,
            List<TriggerFactorResponse> triggerFactors,
            LocalDateTime recordedAt,
            LocalDateTime createdAt
    ) {
        public static Detail from(EmotionLog log, 
                                  List<SomaticSignalResponse> somaticSignals, 
                                  List<TriggerFactorResponse> triggerFactors) {
            return new Detail(
                    log.getId(),
                    log.getEmotionLevel(),
                    log.getEmotionLevel().getScore(),
                    log.getMemo(),
                    somaticSignals,
                    triggerFactors,
                    log.getRecordedAt(),
                    log.getCreatedAt()
            );
        }
    }

    // 2. 타임라인/캘린더용 초경량 요약 응답
    public record TimelineItem(
            Long id,
            int levelScore,
            String levelColorHex,
            List<String> somaticSignalNames,
            List<String> triggerFactorNames,
            boolean hasMemo,
            LocalDateTime recordedAt
    ) {
        public static TimelineItem from(EmotionLog log) {
            return new TimelineItem(
                    log.getId(),
                    log.getEmotionLevel().getScore(),
                    log.getEmotionLevel().getColorHex(),
                    log.getSomaticSignals().stream().map(s -> s.getSomaticSignal().getName()).toList(),
                    log.getTriggerFactors().stream().map(t -> t.getTriggerFactor().getName()).toList(),
                    log.getMemo() != null && !log.getMemo().isBlank(),
                    log.getRecordedAt()
            );
        }
    }
}
}
```

---

## 4. 공통 API 응답 규격 (Standard API Response)

모든 REST API(`@RestController`)는 클라이언트(Vanilla JS / Alpine.js / Flutter)와의 예측 가능한 통신을 위해 `ApiResponse<T>` 포맷을 반환합니다.

### 4.1 JSON 응답 포맷

#### 성공 응답 (200 OK / 201 Created)
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": {
    "id": 1,
    "level": "CALM_NEUTRAL",
    "levelScore": 3,
    "memo": "오랜만에 조용히 책을 읽었다.",
    "tags": [
      { "id": 9, "name": "호흡 편안", "category": "BODY_RESPONSE" },
      { "id": 15, "name": "혼자만의 시간", "category": "TRIGGER" }
    ],
    "recordedAt": "2026-08-28T14:30:00"
  }
}
```

#### 실패 응답 (4xx / 5xx)
```json
{
  "success": false,
  "code": "INVALID_INPUT_VALUE",
  "message": "잘못된 입력값입니다.",
  "data": null,
  "errors": [
    {
      "field": "level",
      "value": 7,
      "reason": "감정 레벨은 5 이하여야 합니다."
    }
  ]
}
```

### 4.2 Java 구현 명세 (`ApiResponse.java`)

```java
package com.colorize.amorfati.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;
    private final List<FieldErrorDetail> errors;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "SUCCESS", "성공", data, null);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, "CREATED", "정상적으로 등록되었습니다.", data, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, "SUCCESS", message, data, null);
    }

    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null, null);
    }

    public static ApiResponse<Void> error(String code, String message, List<FieldErrorDetail> errors) {
        return new ApiResponse<>(false, code, message, null, errors);
    }

    @Getter
    @AllArgsConstructor
    public static class FieldErrorDetail {
        private String field;
        private Object value;
        private String reason;
    }
}
```

---

## 5. 예외 처리 전략 (Error Handling Architecture)

### 5.1 에러 코드 열거형 (`ErrorCode.java`)

```java
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
```

### 5.2 전역 예외 처리기 (`GlobalExceptionHandler.java`)

- `@RestControllerAdvice`를 통해 모든 비즈니스 예외 및 Validation 오류를 가로채어 일관된 `ResponseEntity<ApiResponse<?>>`로 변환합니다.
- 예기치 못한 Exception은 로그를 남기며 `500 INTERNAL_SERVER_ERROR`로 래핑하여 내부 스택 트레이스 노출을 방지합니다.

---

## 6. JPA 엔티티 설계 및 데이터 액세스 규칙

### 6.1 공통 Auditing 엔티티 (`BaseTimeEntity.java`)

```java
package com.colorize.amorfati.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

### 6.2 엔티티 작성 6대 원칙

1. **기본 생성자 접근 제어**: `@NoArgsConstructor(access = AccessLevel.PROTECTED)`를 선언하여 무분별한 객체 생성을 막습니다.
2. **Setter 사용 금지**: `@Setter` 대신 목적이 명확한 비즈니스 수정 메서드(`updateMemo(...)`, `changeEmotionLevel(...)`)를 선언합니다.
3. **정적 팩토리 메서드 활용**: 객체 생성 로직을 캡슐화하기 위해 `of(...)` 또는 `create(...)` 팩토리 메서드를 사용합니다.
4. **모든 연관관계는 지연 로딩(`FetchType.LAZY`)**: N+1 문제를 방지하고 쿼리 실행 흐름을 제어합니다.
5. **다대다(N:M)는 중간 엔티티로 승격**: `EmotionLog`와 `SomaticSignal`은 `EmotionLogSomatic`, `TriggerFactor`는 `EmotionLogTrigger` 연결 엔티티를 두어 다대일 관계로 명확히 풀어냅니다.
6. **조회 전용 트랜잭션 분리**: 조회 서비스 메서드에는 `@Transactional(readOnly = true)`를 반드시 명시합니다.

---

## 7. 보안(Security) & 사용자 식별 전략

### 7.1 Phase 1 (MVP) 보안 정책
- 로컬 개발 단계에서는 개발 편의를 위해 `H2 Console`, 정적 리소스(`/static/**`, `/css/**`, `/js/**`, `/images/**`), 메인 화면을 익명 접근 허용합니다.
- Spring Security OAuth2 / Form Login 연동 전까지는 **기본 테스트 유저(ID: 1, `guest@amorfati.me`)**를 세션/컨텍스트에 자동 주입하는 어댑터를 제공하여 비즈니스 로직 개발에 집중합니다.

### 7.2 `@CurrentUser` 커스텀 애노테이션
컨트롤러 파라미터에서 현재 로그인한 사용자를 깔끔하게 주입받습니다.

```java
@PostMapping("/api/v1/emotions")
public ResponseEntity<ApiResponse<EmotionResponse.Detail>> recordEmotion(
        @CurrentUser Member member,
        @Valid @RequestBody EmotionRequest.Create request) {
    ...
}
```

---

## 8. 컨트롤러 & 뷰 분리 원칙 (SSR vs REST API)

| 분류 | 위치 | 패키지 | 담당 역할 |
| :--- | :--- | :--- | :--- |
| **View Controller** | `@Controller` | `com.colorize.amorfati.web` | Thymeleaf HTML 페이지 렌더링, 초기 화면 구성 데이터 모델 바인딩 |
| **API Controller** | `@RestController` | `com.colorize.amorfati.domain.*.controller` | 비동기 1초 감정 저장, 신체/트리거 프리셋 조회, 타임라인 무한 스크롤 / 월별 필터링 데이터 제공 |

---

## 9. 다음 구현 단계 체크리스트

- [ ] `global/common` 패키지 생성 및 `ApiResponse`, `BaseTimeEntity` 작성
- [ ] `global/error` 패키지 생성 및 `ErrorCode`, `GlobalExceptionHandler`, `BusinessException` 작성
- [ ] `global/config`에 `JpaAuditingConfig`, `SecurityConfig` 설정
- [ ] `domain/member`, `domain/somatic`, `domain/trigger`, `domain/emotion` 순서로 엔티티 및 Repository 구현
- [ ] `EmotionRequest`, `EmotionResponse`, `SomaticSignalResponse`, `TriggerFactorResponse` 작성
