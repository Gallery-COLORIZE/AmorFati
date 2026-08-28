# [UI Design Guide] UI 디자인 시스템 및 컴포넌트 가이드

| 항목 | 내용 |
| :--- | :--- |
| **문서 버전** | v1.0 |
| **작성일** | 2026-08-28 |
| **기반 기술** | Tailwind CSS, DaisyUI (Theme: `dim`), Alpine.js, Lucide Icons, Thymeleaf |
| **문서 목적** | 백엔드 개발자가 직관적으로 구현할 수 있는 저자극 디자인 시스템 & 컴포넌트 표준 정의 |

---

## 1. 디자인 철학 및 원칙 (Design Philosophy)

AmorFati의 UI는 **감각적 자극을 최소화하고, 인지 부하 없이 1초 만에 상태를 기록하는 쉼터**를 지향합니다.

1. **저자극 비주얼 (Low Sensory Overload)**:
   - 눈의 피로를 최소화하는 **DaisyUI `dim` 테마(저채도 다크/슬레이트)** 기본 적용.
   - 강렬한 형광색, 원색, 번쩍이는 깜빡임 애니메이션 완전 배제.
2. **선택의 피로 최소화 (Frictionless 1-Tap Flow)**:
   - 감정 점수 1개만 탭해도 즉시 저장 준비 완료.
   - 텍스트 메모는 100% 선택 사항(빈칸 제출 가능).
3. **무비판적 인터랙션 (Non-Judgmental UI)**:
   - 스트릭(연속 기록) 실패 경고나 붉은색 에러 팝업 금지.
   - 언제 방문해도 편안하게 맞아주는 톤앤매너 유지.

---

## 2. 컬러 팔레트 및 감정 5단계 테마 (Color Palette)

### 2.1 감정 5단계 저채도 컬러 시스템

| 단계 (Level) | 식별 코드 | 명칭 & 상태 | 테마 컬러 | Hex Code | DaisyUI 클래스 매핑 |
| :---: | :--- | :--- | :--- | :---: | :--- |
| **1** | `DEEP_HEAVY` | 몹시 지침 / 괴로움 | 차분한 슬레이트 네이비 | `#334155` | `bg-slate-700 text-slate-100 border-slate-600` |
| **2** | `UNEASY_LOW` | 불안함 / 가라앉음 | 뮤트 더스티 블루 | `#475569` | `bg-slate-600 text-slate-100 border-slate-500` |
| **3** | `CALM_NEUTRAL` | 담담함 / 잔잔함 | 세이지 그린 (자연 톤) | `#4D6A64` | `bg-emerald-900/60 text-emerald-100 border-emerald-700/50` |
| **4** | `COMFORTABLE` | 편안함 / 소소한 온기 | 소프트 샌드 베이지 | `#7C5E57` | `bg-amber-900/40 text-amber-100 border-amber-700/50` |
| **5** | `BRIGHT_ENERGIZED` | 충만함 / 가벼움 | 은은한 올리브 골드 | `#8A7B44` | `bg-yellow-900/40 text-yellow-100 border-yellow-700/50` |

### 2.2 기본 UI 배경 및 텍스트 컬러 (정밀 조율된 웜 코지 테마)
- **🌙 아늑한 밤 모드 (`amorfati-cozy-dark`)**:
  - 누리끼리하거나 탁한 갈색을 배제하고, 차분한 **웜 챠콜(`--b3: #171615`)**과 **소프트 테라코타(`--p: #D97757`)**, **세이지 그린(`--s: #7CA68E`)**으로 아늑하고 현대적인 밤 무드 연출
  - **카드 배경 (`--b1`)**: `#242220` (정돈된 웜 챠콜)
  - **텍스트 (`--bc`)**: `#EDEBE6` (포근한 오이스터 화이트)

- **☀️ 포근한 낮 모드 (`amorfati-cozy-cream`)**:
  - 쨍한 화이트나 어두운 단풍색 대신, 눈이 편안한 **오트밀 크림(`--b3: #F5F2EB`)**과 **부드러운 테라코타 브릭(`--p: #C26343`)**으로 포근한 햇살 감성 연출
  - **카드 배경 (`--b1`)**: `#FFFFFF` (클린 웜 화이트)
  - **텍스트 (`--bc`)**: `#2E2A27` (부드러운 다크 에스프레소)

---

## 3. 핵심 UI 컴포넌트 표준 (Component Specifications)

### 3.1 감정 5단계 선택 바 (`Emotion Selector`)
- **DaisyUI 컴포넌트**: `join grid grid-cols-5`
- **인터랙션**:
  - 선택된 버튼: 볼드 테두리 + 선택 컬러 배경 활성화
  - 미선택 버튼: `btn-ghost` (차분한 아웃라인)

```html
<div class="join w-full grid grid-cols-5 gap-1.5 p-1 bg-base-200 rounded-2xl">
    <button type="button" class="btn join-item btn-sm md:btn-md rounded-xl transition-all duration-200"
            :class="selectedLevel === 1 ? 'bg-slate-700 text-white border-slate-500 shadow-md font-bold' : 'btn-ghost text-base-content/70'"
            @click="selectedLevel = 1">
        <span class="text-base md:text-lg">1</span>
        <span class="text-xs hidden sm:inline">몹시 지침</span>
    </button>
    <button type="button" class="btn join-item btn-sm md:btn-md rounded-xl transition-all duration-200"
            :class="selectedLevel === 2 ? 'bg-slate-600 text-white border-slate-400 shadow-md font-bold' : 'btn-ghost text-base-content/70'"
            @click="selectedLevel = 2">
        <span class="text-base md:text-lg">2</span>
        <span class="text-xs hidden sm:inline">불안함</span>
    </button>
    <button type="button" class="btn join-item btn-sm md:btn-md rounded-xl transition-all duration-200"
            :class="selectedLevel === 3 ? 'bg-emerald-800 text-emerald-100 border-emerald-600 shadow-md font-bold' : 'btn-ghost text-base-content/70'"
            @click="selectedLevel = 3">
        <span class="text-base md:text-lg">3</span>
        <span class="text-xs hidden sm:inline">담담함</span>
    </button>
    <button type="button" class="btn join-item btn-sm md:btn-md rounded-xl transition-all duration-200"
            :class="selectedLevel === 4 ? 'bg-amber-800 text-amber-100 border-amber-600 shadow-md font-bold' : 'btn-ghost text-base-content/70'"
            @click="selectedLevel = 4">
        <span class="text-base md:text-lg">4</span>
        <span class="text-xs hidden sm:inline">편안함</span>
    </button>
    <button type="button" class="btn join-item btn-sm md:btn-md rounded-xl transition-all duration-200"
            :class="selectedLevel === 5 ? 'bg-yellow-800 text-yellow-100 border-yellow-600 shadow-md font-bold' : 'btn-ghost text-base-content/70'"
            @click="selectedLevel = 5">
        <span class="text-base md:text-lg">5</span>
        <span class="text-xs hidden sm:inline">가벼움</span>
    </button>
</div>
```

---

### 3.2 태그 알약 칩 (`Tag Pill Badges`)
- **컴포넌트**: `badge badge-lg rounded-full cursor-pointer`
- **상태 정의**:
  - **기본 상태(Unselected)**: `badge-outline border-base-content/20 text-base-content/70 hover:border-primary`
  - **선택 상태(Selected)**: `badge-primary text-primary-content font-medium shadow-sm`

```html
<div class="space-y-3">
    <div class="flex items-center gap-1.5 text-xs text-base-content/70 font-medium">
        <i data-lucide="activity" class="w-3.5 h-3.5"></i>
        <span>몸에서 느껴지는 신호 (신체 반응)</span>
    </div>
    <div class="flex flex-wrap gap-2">
        <template x-for="tag in bodyTags" :key="tag.id">
            <span class="badge badge-lg py-3 px-3.5 rounded-full cursor-pointer select-none transition-colors text-xs"
                  :class="selectedTagIds.includes(tag.id) ? 'badge-primary' : 'badge-outline border-base-content/25 text-base-content/80 hover:bg-base-200'"
                  @click="toggleTag(tag.id)"
                  x-text="tag.name"></span>
        </template>
    </div>
</div>
```

---

### 3.3 마이크로 메모장 (`Micro Note Area`)
- **컴포넌트**: `textarea textarea-bordered`
- **특징**:
  - 엔터 시 폼 제출 방지, `Ctrl + Enter` (또는 `Cmd + Enter`)로 즉시 저장 지원.
  - 리사이즈 핸들 제한(`resize-none`).

```html
<div class="relative">
    <textarea class="textarea textarea-bordered w-full bg-base-200/50 rounded-xl focus:outline-none focus:border-primary text-sm p-3.5 resize-none leading-relaxed"
              rows="2"
              x-model="memo"
              @keydown.ctrl.enter="saveEmotion()"
              @keydown.meta.enter="saveEmotion()"
              placeholder="하고 싶은 말이 떠오를 때만 적어도 괜찮아요. (Ctrl+Enter 저장)"></textarea>
</div>
```

---

### 3.4 타임라인 및 캘린더 피드 (`Timeline Feed`)
- **컴포넌트**: `timeline timeline-vertical` 또는 미니멀 카드 리스트
- **특징**: 복잡한 그래프 대신, 내가 지나온 시간들의 색상 점(Dot)과 한 줄 기록을 담백하게 렌더링.

```html
<div class="space-y-4">
    <h3 class="text-sm font-semibold text-base-content/80 flex items-center gap-1.5">
        <i data-lucide="clock" class="w-4 h-4"></i>
        <span>오늘의 궤적</span>
    </h3>

    <div class="space-y-2.5">
        <!-- 타임라인 아이템 1건 -->
        <div class="p-4 bg-base-100 border border-base-content/10 rounded-2xl flex items-start justify-between gap-3 hover:border-base-content/20 transition-all">
            <div class="space-y-1.5">
                <div class="flex items-center gap-2">
                    <!-- 감정 색상 도트 -->
                    <span class="w-2.5 h-2.5 rounded-full bg-emerald-500 inline-block"></span>
                    <span class="text-xs font-semibold text-base-content">담담함 (3단계)</span>
                    <span class="text-xs text-base-content/50">14:30</span>
                </div>
                <div class="flex flex-wrap gap-1.5">
                    <span class="badge badge-sm badge-ghost text-xs">호흡 편안</span>
                    <span class="badge badge-sm badge-ghost text-xs">혼자만의 시간</span>
                </div>
                <p class="text-xs text-base-content/80 leading-normal">오랜만에 조용히 책을 읽었다.</p>
            </div>
            <button class="btn btn-ghost btn-xs btn-square text-base-content/40 hover:text-error">
                <i data-lucide="trash-2" class="w-3.5 h-3.5"></i>
            </button>
        </div>
    </div>
</div>
```

---

## 4. 프론트엔드 리소스 로딩 표준 (`templates/layout/default.html`)

백엔드 개발자가 추가적인 Node.js 빌드 없이 바로 사용할 수 있도록, 최신 CDN 리소스를 `<head>`에 포함합니다.

```html
<!DOCTYPE html>
<html lang="ko" data-theme="dim" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title th:text="${title} ?: 'AmorFati 🌿'">AmorFati 🌿</title>

    <!-- 1. DaisyUI + Tailwind CSS -->
    <link href="https://cdn.jsdelivr.net/npm/daisyui@4.12.10/dist/full.min.css" rel="stylesheet" type="text/css" />
    <script src="https://cdn.tailwindcss.com"></script>

    <!-- 2. Lucide Icons (경량 아웃라인 아이콘) -->
    <script src="https://unpkg.com/lucide@latest"></script>

    <!-- 3. Alpine.js (초경량 클라이언트 상태 관리) -->
    <script defer src="https://cdn.jsdelivr.net/npm/alpinejs@3.14.1/dist/cdn.min.js"></script>

    <!-- 4. 저자극 커스텀 스타일 (폰트 및 휠 스크롤 부드럽게) -->
    <style>
        @import url('https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/static/pretendard.css');
        body {
            font-family: "Pretendard Variable", Pretendard, -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;
            letter-spacing: -0.015em;
        }
    </style>
</head>
<body class="min-h-screen bg-base-300 text-base-content flex flex-col items-center justify-start antialiased selection:bg-primary/30">

    <!-- 상단 글로벌 헤더 -->
    <header class="w-full max-w-xl px-4 py-6 flex items-center justify-between">
        <a href="/" class="flex items-center gap-2 group">
            <span class="text-xl font-bold tracking-tight text-primary">AmorFati</span>
            <span class="text-xs text-base-content/60 group-hover:text-base-content transition-colors">🌿 네 운명을 사랑하라</span>
        </a>
        <div class="text-xs text-base-content/50" th:text="${#temporals.format(#temporals.createNow(), 'yyyy.MM.dd (E)')}">
            2026.08.28 (금)
        </div>
    </header>

    <!-- 메인 컨텐츠 영역 -->
    <main class="w-full max-w-xl px-4 pb-16">
        <th:block th:replace="${content}"></th:block>
    </main>

    <!-- Lucide 아이콘 초기화 스크립트 -->
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            lucide.createIcons();
        });
    </script>
</body>
</html>
```

---

## 5. Alpine.js 비동기 기록 로직 표준 (JavaScript)

```javascript
function emotionApp() {
    return {
        selectedLevel: 3,
        selectedTagIds: [],
        memo: '',
        isSubmitting: false,
        bodyTags: [],
        triggerTags: [],
        timelineLogs: [],

        init() {
            this.fetchTags();
            this.fetchTodayLogs();
        },

        toggleTag(tagId) {
            if (this.selectedTagIds.includes(tagId)) {
                this.selectedTagIds = this.selectedTagIds.filter(id => id !== tagId);
            } else {
                this.selectedTagIds.push(tagId);
            }
        },

        async saveEmotion() {
            if (this.isSubmitting) return;
            this.isSubmitting = true;

            const payload = {
                level: this.selectedLevel,
                tagIds: this.selectedTagIds,
                memo: this.memo.trim() ? this.memo.trim() : null
            };

            try {
                const res = await fetch('/api/v1/emotions', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
                const result = await res.json();

                if (result.success) {
                    this.memo = '';
                    this.selectedTagIds = [];
                    this.fetchTodayLogs(); // 타임라인 갱신
                } else {
                    alert(result.message || '저장에 실패했습니다.');
                }
            } catch (err) {
                console.error('기록 저장 중 오류:', err);
            } finally {
                this.isSubmitting = false;
            }
        },

        async fetchTags() {
            const res = await fetch('/api/v1/tags');
            const result = await res.json();
            if (result.success) {
                this.bodyTags = result.data.filter(t => t.category === 'BODY_RESPONSE');
                this.triggerTags = result.data.filter(t => t.category === 'TRIGGER');
                this.$nextTick(() => lucide.createIcons());
            }
        },

        async fetchTodayLogs() {
            const res = await fetch('/api/v1/emotions/today');
            const result = await res.json();
            if (result.success) {
                this.timelineLogs = result.data;
                this.$nextTick(() => lucide.createIcons());
            }
        }
    }
}
```

---

## 6. 다음 구현 단계 체크리스트

- [ ] `src/main/resources/templates/layout/default.html` 레이아웃 템플릿 생성
- [ ] `src/main/resources/templates/index.html` 메인 화면 뷰 작성
- [ ] `src/main/resources/static/js/app.js` Alpine.js 프론트 인터랙션 스크립트 작성
