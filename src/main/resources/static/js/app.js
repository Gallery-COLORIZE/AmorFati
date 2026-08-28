/**
 * AmorFati (아모르파티) 🌿 - Main Application Script
 */

function heroApp() {
    return {
        currentTheme: localStorage.getItem('amorfati_theme') || 'amorfati-oatmeal',
        selectedLevel: 3,
        selectedTags: [],
        memo: '',
        isSubmitting: false,
        showToast: false,

        // 신체 반응 태그 목록
        bodyTags: [
            '가슴 답답함', '호흡이 얕음', '두통/머리 무거움',
            '턱/어깨 긴장', '위장 불편감', '눈 피로',
            '온몸 무기력', '깊은 이완/호흡 편안', '몸이 가벼움'
        ],

        // 상황 및 트리거 태그 목록
        triggerTags: [
            '대인관계/대화', '소음/외부 자극', '업무/과부하',
            '수면 부족', '혼자만의 시간', '모터사이클/라이딩',
            '자연/산책', '휴식/멍때리기'
        ],

        // 데모 초기 타임라인 샘플 데이터
        records: [
            {
                level: 3,
                levelTitle: '담담함 (3단계)',
                dotClass: 'bg-[#597A6B]',
                tags: ['호흡 편안', '혼자만의 시간'],
                memo: '오랜만에 조용히 책을 읽고 차를 마셨다.',
                time: '14:30'
            },
            {
                level: 2,
                levelTitle: '불안/답답 (2단계)',
                dotClass: 'bg-[#5F7184]',
                tags: ['가슴 답답함', '소음/외부 자극'],
                memo: '',
                time: '11:15'
            }
        ],

        init() {
            document.documentElement.setAttribute('data-theme', this.currentTheme);
            this.$nextTick(() => {
                lucide.createIcons();
            });
        },

        setTheme(themeName) {
            this.currentTheme = themeName;
            document.documentElement.setAttribute('data-theme', themeName);
            localStorage.setItem('amorfati_theme', themeName);
            this.$nextTick(() => {
                lucide.createIcons();
            });
        },

        toggleTag(tag) {
            if (this.selectedTags.includes(tag)) {
                this.selectedTags = this.selectedTags.filter(t => t !== tag);
            } else {
                this.selectedTags.push(tag);
            }
        },

        getLevelName(level) {
            const names = {
                1: '1단계: 몹시 지침/괴로움',
                2: '2단계: 불안함/가라앉음',
                3: '3단계: 담담함/잔잔함',
                4: '4단계: 편안함/소소한 온기',
                5: '5단계: 충만함/가벼움'
            };
            return names[level] || '';
        },

        getLevelDotClass(level) {
            const dots = {
                1: 'bg-[#6D6574]',
                2: 'bg-[#5F7184]',
                3: 'bg-[#597A6B]',
                4: 'bg-[#8F6A55]',
                5: 'bg-[#9E7D44]'
            };
            return dots[level] || 'theme-primary-bg';
        },

        saveEmotion() {
            if (this.isSubmitting) return;
            this.isSubmitting = true;

            setTimeout(() => {
                const now = new Date();
                const timeStr = String(now.getHours()).padStart(2, '0') + ':' + String(now.getMinutes()).padStart(2, '0');

                this.records.unshift({
                    level: this.selectedLevel,
                    levelTitle: this.getLevelName(this.selectedLevel),
                    dotClass: this.getLevelDotClass(this.selectedLevel),
                    tags: [...this.selectedTags],
                    memo: this.memo.trim(),
                    time: timeStr
                });

                this.memo = '';
                this.selectedTags = [];
                this.isSubmitting = false;
                this.showToast = true;

                this.$nextTick(() => {
                    lucide.createIcons();
                });

                setTimeout(() => {
                    this.showToast = false;
                }, 3500);
            }, 250);
        },

        deleteRecord(index) {
            this.records.splice(index, 1);
            this.$nextTick(() => {
                lucide.createIcons();
            });
        }
    };
}

document.addEventListener('DOMContentLoaded', () => {
    lucide.createIcons();
});
