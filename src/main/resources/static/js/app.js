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

        // 1. 신체 반응 신호 (DB API 연동)
        somaticSignals: [],

        // 2. 상황 및 트리거 요인 (DB API 연동)
        triggerFactors: [],

        // 3. 오늘 남긴 타임라인 기록 (DB API 연동)
        records: [],

        async init() {
            document.documentElement.setAttribute('data-theme', this.currentTheme);
            await this.loadPresets();
            await this.loadTodayRecords();
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

        // 신체 반응 및 트리거 프리셋 목록 DB 조회
        async loadPresets() {
            try {
                const [somaticRes, triggerRes] = await Promise.all([
                    fetch('/api/somatic-signals').then(r => r.json()),
                    fetch('/api/trigger-factors').then(r => r.json())
                ]);

                if (somaticRes.success && somaticRes.data) {
                    this.somaticSignals = somaticRes.data;
                }
                if (triggerRes.success && triggerRes.data) {
                    this.triggerFactors = triggerRes.data;
                }
            } catch (error) {
                console.error('프리셋 데이터를 불러오지 못했습니다:', error);
            }
        },

        // 오늘의 감정 궤적 목록 DB 조회
        async loadTodayRecords() {
            try {
                const res = await fetch('/api/emotions/today').then(r => r.json());
                if (res.success && res.data) {
                    this.records = res.data;
                }
            } catch (error) {
                console.error('오늘의 기록을 불러오지 못했습니다:', error);
            }
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

        // 1초 감정 기록 저장 (POST /api/emotions)
        async saveEmotion() {
            if (this.isSubmitting) return;
            this.isSubmitting = true;

            const payload = {
                level: this.selectedLevel,
                somaticSignalIds: this.selectedSomaticIds,
                triggerFactorIds: this.selectedTriggerIds,
                memo: this.memo.trim() ? this.memo.trim() : null
            };

            try {
                const response = await fetch('/api/emotions', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
                const result = await response.json();

                if (result.success && result.data) {
                    this.records.unshift(result.data);
                    this.memo = '';
                    this.selectedSomaticIds = [];
                    this.selectedTriggerIds = [];
                    this.showToast = true;

                    this.$nextTick(() => {
                        lucide.createIcons();
                    });

                    setTimeout(() => {
                        this.showToast = false;
                    }, 3500);
                } else {
                    console.error('기록 저장 실패:', result.message);
                }
            } catch (error) {
                console.error('서버 통신 오류:', error);
            } finally {
                this.isSubmitting = false;
            }
        },

        // 감정 기록 삭제 (DELETE /api/emotions/{id})
        async deleteRecord(index, id) {
            if (id) {
                try {
                    await fetch('/api/emotions/' + id, { method: 'DELETE' });
                } catch (error) {
                    console.error('기록 삭제 실패:', error);
                }
            }
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
