

-- 신체 반응 9종 프리셋 (Somatic Signals)
INSERT INTO somatic_signal (id, name, icon, display_order, created_at) VALUES
(1, '가슴 답답함', 'heart-crack', 1, CURRENT_TIMESTAMP),
(2, '호흡이 얕음', 'wind', 2, CURRENT_TIMESTAMP),
(3, '두통/머리 무거움', 'zap-off', 3, CURRENT_TIMESTAMP),
(4, '턱/어깨 긴장', 'activity', 4, CURRENT_TIMESTAMP),
(5, '위장 불편감', 'frown', 5, CURRENT_TIMESTAMP),
(6, '눈 피로', 'eye-off', 6, CURRENT_TIMESTAMP),
(7, '온몸 무기력', 'battery-low', 7, CURRENT_TIMESTAMP),
(8, '깊은 이완/호흡 편안', 'smile', 8, CURRENT_TIMESTAMP),
(9, '몸이 가벼움', 'feather', 9, CURRENT_TIMESTAMP);

-- 상황 및 트리거 8종 프리셋 (Trigger Factors)
INSERT INTO trigger_factor (id, name, icon, display_order, created_at) VALUES
(1, '대인관계/대화', 'users', 1, CURRENT_TIMESTAMP),
(2, '소음/외부 자극', 'volume-2', 2, CURRENT_TIMESTAMP),
(3, '업무/과부하', 'briefcase', 3, CURRENT_TIMESTAMP),
(4, '수면 부족', 'moon', 4, CURRENT_TIMESTAMP),
(5, '혼자만의 시간', 'coffee', 5, CURRENT_TIMESTAMP),
(6, '모터사이클/라이딩', 'navigation', 6, CURRENT_TIMESTAMP),
(7, '자연/산책', 'trees', 7, CURRENT_TIMESTAMP),
(8, '휴식/멍때리기', 'sun', 8, CURRENT_TIMESTAMP);
