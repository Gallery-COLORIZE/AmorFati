# [Architecture Standards] 시스템 아키텍처 및 백엔드 개발 표준

| 항목 | 내용 |
| :--- | :--- |
| **문서 버전** | v2.1 (REST API + Auto Response Wrapping) |
| **작성일** | 2026-09-02 |
| **기반 기술** | Java 25, Spring Boot 4.1.1, Spring Data JPA, Spring Security, Alpine.js (Fetch API) |
| **문서 목적** | AmorFati 백엔드 아키텍처, 패키지 구조, DTO 패턴, 자동 ApiResponse 래핑(`ResponseBodyAdvice`) 및 컨벤션 확립 |

---

## 1. 개요 및 설계 철학

AmorFati는 1인 개발의 생산성과 확장성을 위해 **도메인 주도형 패키지 구조**와 **자동 공통 응답 래핑(`ResponseBodyAdvice`)** 아키텍처를 채택합니다.

- **컨트롤러 단순화 (Zero Boilerplate)**: 컨트롤러에서 매번 `ApiResponse<T>`나 `ResponseEntity`로 감싸지 않고 **순수 도메인 DTO(data)만 반환**하면, Spring의 `GlobalResponseAdvice`가 자동으로 `{ success, code, message, data }` 구조로 감싸서 반환합니다.
- **클라이언트 비동기 통신**: 별도의 무거운 외부 라이브러리 없이 브라우저 내장 **Native `fetch` API**와 Alpine.js를 연동하여 부드러운 SPA 경험을 제공합니다.
- **모던 Java 25 Record DTO**: 불변 `record`와 Compact Constructor를 활용한 엄격한 유효성 검증 및 기본값 처리.
- **안정적인 데이터 모델링**: 모든 연관관계 지연 로딩(`LAZY`) 및 타임라인 조회 시 N+1 방지 쿼리 적용.

---

## 2. 패키지 구조 (Package Architecture)

```text
com.colorize.amorfati
├── AmorFatiApplication.java
│
├── domain/                      # 핵심 비즈니스 도메인 (도메인별 응집)
│   ├── emotion/                 # 감정 기록 도메인
│   │   ├── controller/          # REST API (@RestController)
│   │   │   └── EmotionApiController.java
│   │   ├── dto/                 # Record 기반 DTO
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
├── web/                         # 화면 진입용 컨트롤러
│   └── HomeController.java      # 메인 index.html 렌더링
│
└── global/                      # 전역 공통 인프라
    ├── auth/                    # 인증 및 Security 컨텍스트 헬퍼
    │   └── CurrentUser.java     # @CurrentUser 커스텀 애노테이션
    ├── common/                  # 공통 응답 및 어드바이스
    │   ├── ApiResponse.java     # 공통 응답 포맷 DTO
    │   ├── GlobalResponseAdvice.java # 자동 ApiResponse 래핑 ResponseBodyAdvice
    │   ├── BaseCreatedEntity.java
    │   └── BaseTimeEntity.java  # 생성일/수정일 Auditing
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

## 3. 공통 API 응답 규격 및 자동 래핑 (`GlobalResponseAdvice`)

### 3.1 컨트롤러 작성 표준 (Data만 반환)
컨트롤러는 불필요하게 `ApiResponse.success(...)`를 작성할 필요 없이, **순수 DTO(또는 `List<T>`)**만 반환합니다.

```java
@RestController
@RequestMapping("/api/v1/emotions")
@RequiredArgsConstructor
public class EmotionApiController {

    private final EmotionService emotionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmotionResponse.Detail create(@Valid @RequestBody EmotionRequest.Create request) {
        return emotionService.recordEmotion(request);
    }

    @GetMapping("/today")
    public List<EmotionResponse.TimelineItem> getTodayEmotions() {
        return emotionService.getTodayTimeline();
    }
}
```

### 3.2 실제 클라이언트로 나가는 JSON 포맷

`GlobalResponseAdvice`가 인터셉트하여 아래 포맷으로 자동 변환합니다.

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": {
    "id": 1,
    "levelScore": 3,
    "levelTitle": "담담함/잔잔함",
    "memo": "오랜만에 조용히 책을 읽었다.",
    "somaticSignals": [{ "name": "호흡 편안", "icon": "smile" }],
    "triggerFactors": [{ "name": "혼자만의 시간", "icon": "coffee" }],
    "time": "14:30"
  }
}
```

---

## 4. 모던 DTO 설계 표준 (Java 25 Record)

1. **불변성 보장**: 모든 DTO는 `record`로 작성.
2. **도메인 단위 네임스페이스 그룹화**: 요청은 `[Domain]Request`, 응답은 `[Domain]Response` 래퍼 클래스 내부의 static record로 선언.
3. **단방향 매핑 책임 분리**:
   - `Request`: `toEntity(...)` 메서드로 엔티티 변환.
   - `Response`: `from(entity, ...)` 정적 팩토리 메서드로 DTO 생성.

---

## 5. 예외 처리 전략 (`GlobalExceptionHandler`)

- `@RestControllerAdvice`에서 비즈니스 예외(`BusinessException`), 유효성 검증 실패(`MethodArgumentNotValidException`)를 가로채어 규격화된 에러 응답을 반환합니다.

```json
{
  "success": false,
  "code": "INVALID_INPUT_VALUE",
  "message": "입력값이 올바르지 않습니다.",
  "data": null,
  "errors": [
    {
      "field": "level",
      "value": 6,
      "reason": "감정 레벨은 5 이하여야 합니다."
    }
  ]
}
```

---

## 6. 다음 구현 단계 체크리스트

- [x] `global/common/ApiResponse.java` (공통 응답 래퍼) 구현
- [x] `global/common/GlobalResponseAdvice.java` (자동 래핑 ResponseBodyAdvice) 구현
- [x] `global/error/ErrorCode.java`, `BusinessException`, `GlobalExceptionHandler` 구현
- [ ] `domain/*/dto` DTO 레코드 작성 (`EmotionRequest`, `EmotionResponse`, `SomaticSignalResponse`, `TriggerFactorResponse`)
- [ ] `domain/*/service` 비즈니스 로직 구현 (`EmotionService`, `SomaticSignalService`, `TriggerFactorService`)
- [ ] `domain/*/controller` REST 컨트롤러 구현
- [ ] `app.js`에서 브라우저 내장 `fetch` API 연동 및 Mock 데이터 교체
