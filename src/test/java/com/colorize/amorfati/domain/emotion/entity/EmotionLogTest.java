package com.colorize.amorfati.domain.emotion.entity;

import com.colorize.amorfati.domain.member.entity.Member;
import com.colorize.amorfati.domain.somatic.entity.SomaticSignal;
import com.colorize.amorfati.domain.trigger.entity.TriggerFactor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EmotionLogTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@colorize.com")
                .nickname("테스터")
                .authProvider("TEST")
                .providerId("test-sub-1")
                .build();
    }

    @Test
    @DisplayName("EmotionLog 생성 시 levelScore가 level의 점수와 일치하고 recordedAt이 기본값으로 설정된다")
    void createEmotionLog_Success() {
        // given
        EmotionLevel level = EmotionLevel.COMFORTABLE; // 4

        // when
        EmotionLog log = EmotionLog.create(member, level, "오늘 하루 기분 좋았다.", null);

        // then
        assertThat(log.getMember()).isEqualTo(member);
        assertThat(log.getEmotionLevel()).isEqualTo(level);
        assertThat(log.getLevelScore()).isEqualTo(4);
        assertThat(log.getMemo()).isEqualTo("오늘 하루 기분 좋았다.");
        assertThat(log.getRecordedAt()).isNotNull();
        assertThat(log.getSomaticSignals()).isEmpty();
        assertThat(log.getTriggerFactors()).isEmpty();
    }

    @Test
    @DisplayName("기록 시간(recordedAt)을 지정하여 생성하면 지정된 시각이 적용된다")
    void createEmotionLog_WithCustomRecordedAt() {
        // given
        LocalDateTime customTime = LocalDateTime.of(2026, 9, 1, 14, 30);

        // when
        EmotionLog log = EmotionLog.create(member, EmotionLevel.CALM_NEUTRAL, "테스트 메모", customTime);

        // then
        assertThat(log.getRecordedAt()).isEqualTo(customTime);
    }

    @Test
    @DisplayName("addSomaticSignal 호출 시 중간 매핑 엔티티(EmotionLogSomatic)가 생성되어 컬렉션에 추가된다")
    void addSomaticSignal() {
        // given
        EmotionLog log = EmotionLog.create(member, EmotionLevel.COMFORTABLE, "테스트", null);
        SomaticSignal signal = SomaticSignal.of("가슴 답답함", "heart-crack", 1);

        // when
        log.addSomaticSignal(signal);

        // then
        assertThat(log.getSomaticSignals()).hasSize(1);
        assertThat(log.getSomaticSignals().get(0).getEmotionLog()).isEqualTo(log);
        assertThat(log.getSomaticSignals().get(0).getSomaticSignal()).isEqualTo(signal);
    }

    @Test
    @DisplayName("addTriggerFactor 호출 시 중간 매핑 엔티티(EmotionLogTrigger)가 생성되어 컬렉션에 추가된다")
    void addTriggerFactor() {
        // given
        EmotionLog log = EmotionLog.create(member, EmotionLevel.COMFORTABLE, "테스트", null);
        TriggerFactor trigger = TriggerFactor.of("자연/산책", "trees", 7);

        // when
        log.addTriggerFactor(trigger);

        // then
        assertThat(log.getTriggerFactors()).hasSize(1);
        assertThat(log.getTriggerFactors().get(0).getEmotionLog()).isEqualTo(log);
        assertThat(log.getTriggerFactors().get(0).getTriggerFactor()).isEqualTo(trigger);
    }

    @Test
    @DisplayName("updateMemo 호출 시 메모가 정상 변경된다")
    void updateMemo() {
        // given
        EmotionLog log = EmotionLog.create(member, EmotionLevel.COMFORTABLE, "초기 메모", null);

        // when
        log.updateMemo("수정된 메모");

        // then
        assertThat(log.getMemo()).isEqualTo("수정된 메모");
    }

    @Test
    @DisplayName("changeEmotionLevel 호출 시 감정 레벨과 levelScore가 동기화되어 변경된다")
    void changeEmotionLevel() {
        // given
        EmotionLog log = EmotionLog.create(member, EmotionLevel.DEEP_HEAVY, "힘든 날", null);
        assertThat(log.getLevelScore()).isEqualTo(1);

        // when
        log.changeEmotionLevel(EmotionLevel.BRIGHT_ENERGIZED);

        // then
        assertThat(log.getEmotionLevel()).isEqualTo(EmotionLevel.BRIGHT_ENERGIZED);
        assertThat(log.getLevelScore()).isEqualTo(5);
    }
}
