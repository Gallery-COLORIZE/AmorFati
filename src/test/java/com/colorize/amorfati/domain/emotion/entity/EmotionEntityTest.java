package com.colorize.amorfati.domain.emotion.entity;

import com.colorize.amorfati.domain.emotion.repository.EmotionLogRepository;
import com.colorize.amorfati.domain.member.entity.Member;
import com.colorize.amorfati.domain.member.repository.MemberRepository;
import com.colorize.amorfati.domain.somatic.entity.SomaticSignal;
import com.colorize.amorfati.domain.somatic.repository.SomaticSignalRepository;
import com.colorize.amorfati.domain.trigger.entity.TriggerFactor;
import com.colorize.amorfati.domain.trigger.repository.TriggerFactorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class EmotionEntityTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SomaticSignalRepository somaticSignalRepository;

    @Autowired
    private TriggerFactorRepository triggerFactorRepository;

    @Autowired
    private EmotionLogRepository emotionLogRepository;

    @Test
    @DisplayName("기본 시드 데이터(Mock 사용자, 신체 반응 9종, 트리거 8종)가 정상 로드된다")
    void seedDataLoadTest() {
        // then
        Member member = memberRepository.findByEmail("guest@amorfati.me").orElseThrow();
        assertThat(member.getNickname()).isEqualTo("방랑자");

        List<SomaticSignal> somaticSignals = somaticSignalRepository.findAllByOrderByDisplayOrderAsc();
        assertThat(somaticSignals).hasSize(9);
        assertThat(somaticSignals.get(0).getName()).isEqualTo("가슴 답답함");
        assertThat(somaticSignals.get(0).getIcon()).isEqualTo("heart-crack");

        List<TriggerFactor> triggerFactors = triggerFactorRepository.findAllByOrderByDisplayOrderAsc();
        assertThat(triggerFactors).hasSize(8);
        assertThat(triggerFactors.get(0).getName()).isEqualTo("대인관계/대화");
        assertThat(triggerFactors.get(0).getIcon()).isEqualTo("users");
    }

    @Test
    @DisplayName("감정 기록을 신체 반응 및 트리거 요인과 함께 성공적으로 저장 및 조회한다")
    void saveAndQueryEmotionLogTest() {
        // given
        Member member = memberRepository.findByEmail("guest@amorfati.me").orElseThrow();
        SomaticSignal somatic1 = somaticSignalRepository.findAllByOrderByDisplayOrderAsc().get(0); // 가슴 답답함
        TriggerFactor trigger1 = triggerFactorRepository.findAllByOrderByDisplayOrderAsc().get(1); // 소음/외부 자극

        EmotionLog emotionLog = EmotionLog.builder()
                .member(member)
                .emotionLevel(EmotionLevel.UNEASY_LOW)
                .memo("소음 때문에 약간 가슴이 답답하고 불안했다.")
                .recordedAt(LocalDateTime.now())
                .build();

        emotionLog.addSomaticSignal(somatic1);
        emotionLog.addTriggerFactor(trigger1);

        // when
        EmotionLog saved = emotionLogRepository.save(emotionLog);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmotionLevel()).isEqualTo(EmotionLevel.UNEASY_LOW);
        assertThat(saved.getLevelScore()).isEqualTo(2);
        assertThat(saved.getSomaticSignals()).hasSize(1);
        assertThat(saved.getTriggerFactors()).hasSize(1);

        // 기간별 타임라인 조회 검증 (default_batch_fetch_size: 100 동작)
        List<EmotionLog> logs = emotionLogRepository.findAllByMemberAndRecordedAtBetweenOrderByRecordedAtDesc(
                member,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getSomaticSignals().get(0).getSomaticSignal().getName()).isEqualTo("가슴 답답함");
        assertThat(logs.get(0).getTriggerFactors().get(0).getTriggerFactor().getName()).isEqualTo("소음/외부 자극");
    }
}
