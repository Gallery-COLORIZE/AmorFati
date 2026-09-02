/**
 * AmorFati (아모르파티) 🌿 - Main Application Script
 */

function heroApp() {
    return {
        currentTheme: localStorage.getItem('amorfati_theme') || 'amorfati-oatmeal',
        selectedLevel: 3,
        selectedSomaticIds: [],
        selectedTriggerIds: [],
        memo: '',
        isSubmitting: false,
        showToast: false,

        // 1. 신체 반응 신호 9종 프리셋 (Somatic Signals)
        somaticSignals: [
            { id: 1, name: '가슴 답답함', icon: 'heart-crack' },
            { id: 2, name: '호흡이 얕음', icon: 'wind' },
            { id: 3, name: '두통/머리 무거움', icon: 'zap-off' },
            { id: 4, name: '턱/어깨 긴장', icon: 'activity' },
            { id: 5, name: '위장 불편감', icon: 'frown' },
            { id: 6, name: '눈 피로', icon: 'eye-off' },
            { id: 7, name: '온몸 무기력', icon: 'battery-low' },
            { id: 8, name: '깊은 이완/호흡 편안', icon: 'smile' },
            { id: 9, name: '몸이 가벼움', icon: 'feather' }
        ],

        // 2. 상황 및 트리거 요인 8종 프리셋 (Trigger Factors)
        triggerFactors: [
            { id: 1, name: '대인관계/대화', icon: 'users' },
            { id: 2, name: '소음/외부 자극', icon: 'volume-2' },
            { id: 3, name: '업무/과부하', icon: 'briefcase' },
            { id: 4, name: '수면 부족', icon: 'moon' },
            { id: 5, name: '혼자만의 시간', icon: 'coffee' },
            { id: 6, name: '모터사이클/라이딩', icon: 'navigation' },
            { id: 7, name: '자연/산책', icon: 'trees' },
            { id: 8, name: '휴식/멍때리기', icon: 'sun' }
        ],

        // 데모 초기 타임라인 샘플 데이터
        records: [
            {
                level: 3,
                levelTitle: '3단계: 담담함/잔잔함',
                dotClass: 'bg-[#597A6B]',
                levelIcon: 'leaf',
                somaticSignals: [{ name: '깊은 이완/호흡 편안', icon: 'smile' }],
                triggerFactors: [{ name: '혼자만의 시간', icon: 'coffee' }],
                memo: '오랜만에 조용히 책을 읽고 차를 마셨다.',
                time: '14:30'
            },
            {
                level: 2,
                levelTitle: '2단계: 불안함/가라앉음',
                dotClass: 'bg-[#5F7184]',
                levelIcon: 'wind',
                somaticSignals: [{ name: '가슴 답답함', icon: 'heart-crack' }],
                triggerFactors: [{ name: '소음/외부 자극', icon: 'volume-2' }],
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

        toggleSomatic(id) {
            if (this.selectedSomaticIds.includes(id)) {
                this.selectedSomaticIds = this.selectedSomaticIds.filter(i => i !== id);
            } else {
                this.selectedSomaticIds.push(id);
            }
        },

        toggleTrigger(id) {
            if (this.selectedTriggerIds.includes(id)) {
                this.selectedTriggerIds = this.selectedTriggerIds.filter(i => i !== id);
            } else {
                this.selectedTriggerIds.push(id);
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

        getLevelIcon(level) {
            const icons = {
                1: 'cloud-rain',
                2: 'wind',
                3: 'leaf',
                4: 'heart',
                5: 'sparkles'
            };
            return icons[level] || 'sparkles';
        },

        saveEmotion() {
            if (this.isSubmitting) return;
            this.isSubmitting = true;

            setTimeout(() => {
                const now = new Date();
                const timeStr = String(now.getHours()).padStart(2, '0') + ':' + String(now.getMinutes()).padStart(2, '0');

                const chosenSomatic = this.somaticSignals.filter(s => this.selectedSomaticIds.includes(s.id));
                const chosenTrigger = this.triggerFactors.filter(t => this.selectedTriggerIds.includes(t.id));

                this.records.unshift({
                    level: this.selectedLevel,
                    levelTitle: this.getLevelName(this.selectedLevel),
                    dotClass: this.getLevelDotClass(this.selectedLevel),
                    levelIcon: this.getLevelIcon(this.selectedLevel),
                    somaticSignals: chosenSomatic,
                    triggerFactors: chosenTrigger,
                    memo: this.memo.trim(),
                    time: timeStr
                });

                this.memo = '';
                this.selectedSomaticIds = [];
                this.selectedTriggerIds = [];
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
