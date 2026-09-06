package com.colorize.amorfati.domain.emotion.service;

import com.colorize.amorfati.domain.emotion.dto.EmotionRequest;
import com.colorize.amorfati.domain.emotion.dto.EmotionResponse;
import com.colorize.amorfati.domain.emotion.entity.EmotionLevel;
import com.colorize.amorfati.domain.emotion.entity.EmotionLog;
import com.colorize.amorfati.domain.emotion.repository.EmotionLogRepository;
import com.colorize.amorfati.domain.member.entity.Member;
import com.colorize.amorfati.domain.member.service.MemberService;
import com.colorize.amorfati.domain.somatic.entity.SomaticSignal;
import com.colorize.amorfati.domain.somatic.repository.SomaticSignalRepository;
import com.colorize.amorfati.domain.trigger.entity.TriggerFactor;
import com.colorize.amorfati.domain.trigger.repository.TriggerFactorRepository;
import com.colorize.amorfati.global.error.ErrorCode;
import com.colorize.amorfati.global.error.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EmotionServiceTest {

    @Mock
    private EmotionLogRepository emotionLogRepository;

    @Mock
    private SomaticSignalRepository somaticSignalRepository;

    @Mock
    private TriggerFactorRepository triggerFactorRepository;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private EmotionService emotionService;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .email("test@colorize.com")
                .nickname("테스터")
                .authProvider("LOCAL")
                .build();
    }

    @Test
    @DisplayName("감정 기록 생성: 신체 반응 및 트리거 ID가 주어지면 매핑 엔티티를 구성하여 저장한다")
    void recordEmotion_WithTags_Success() {
        // given
        EmotionRequest.Create request = new EmotionRequest.Create(
                4,
                List.of(1L, 2L),
                List.of(3L),
                "편안한 저녁 산책",
                LocalDateTime.of(2026, 9, 6, 19, 0)
        );

        SomaticSignal signal1 = SomaticSignal.of("가슴 답답함", "heart-crack", 1);
        SomaticSignal signal2 = SomaticSignal.of("호흡이 얕음", "wind", 2);
        TriggerFactor trigger = TriggerFactor.of("업무/과부하", "briefcase", 3);

        given(memberService.getOrCreateDefaultMember()).willReturn(testMember);
        given(somaticSignalRepository.findAllByIdIn(request.somaticSignalIds())).willReturn(List.of(signal1, signal2));
        given(triggerFactorRepository.findAllByIdIn(request.triggerFactorIds())).willReturn(List.of(trigger));
        given(emotionLogRepository.save(any(EmotionLog.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        EmotionResponse.TimelineItem response = emotionService.recordEmotion(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.level()).isEqualTo(4);
        assertThat(response.memo()).isEqualTo("편안한 저녁 산책");
        assertThat(response.somaticSignals()).hasSize(2);
        assertThat(response.triggerFactors()).hasSize(1);

        then(memberService).should().getOrCreateDefaultMember();
        then(somaticSignalRepository).should().findAllByIdIn(request.somaticSignalIds());
        then(triggerFactorRepository).should().findAllByIdIn(request.triggerFactorIds());
        then(emotionLogRepository).should().save(any(EmotionLog.class));
    }

    @Test
    @DisplayName("감정 기록 생성: 신체 반응 및 트리거가 없어도 정상 저장된다")
    void recordEmotion_WithoutTags_Success() {
        // given
        EmotionRequest.Create request = new EmotionRequest.Create(
                3,
                List.of(),
                List.of(),
                "담담한 하루",
                null
        );

        given(memberService.getOrCreateDefaultMember()).willReturn(testMember);
        given(emotionLogRepository.save(any(EmotionLog.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        EmotionResponse.TimelineItem response = emotionService.recordEmotion(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.level()).isEqualTo(3);
        assertThat(response.memo()).isEqualTo("담담한 하루");
        assertThat(response.somaticSignals()).isEmpty();
        assertThat(response.triggerFactors()).isEmpty();

        then(somaticSignalRepository).shouldHaveNoInteractions();
        then(triggerFactorRepository).shouldHaveNoInteractions();
        then(emotionLogRepository).should().save(any(EmotionLog.class));
    }

    @Test
    @DisplayName("오늘 타임라인 조회: 회원의 오늘 시작과 끝 범위로 감정 기록 목록을 조회한다")
    void getTodayTimeline_Success() {
        // given
        EmotionLog log1 = EmotionLog.create(testMember, EmotionLevel.COMFORTABLE, "기록1", LocalDateTime.now().minusHours(1));
        EmotionLog log2 = EmotionLog.create(testMember, EmotionLevel.BRIGHT_ENERGIZED, "기록2", LocalDateTime.now());

        given(memberService.getOrCreateDefaultMember()).willReturn(testMember);
        given(emotionLogRepository.findByMemberAndPeriod(eq(testMember), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of(log2, log1));

        // when
        List<EmotionResponse.TimelineItem> timeline = emotionService.getTodayTimeline();

        // then
        assertThat(timeline).hasSize(2);
        assertThat(timeline.get(0).memo()).isEqualTo("기록2");
        assertThat(timeline.get(1).memo()).isEqualTo("기록1");

        then(emotionLogRepository).should().findByMemberAndPeriod(eq(testMember), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("감정 상세 조회: 존재하는 ID이면 상세 정보를 반환한다")
    void getEmotionDetail_Success() {
        // given
        Long logId = 1L;
        EmotionLog log = EmotionLog.create(testMember, EmotionLevel.COMFORTABLE, "상세 메모", LocalDateTime.now());

        given(memberService.getOrCreateDefaultMember()).willReturn(testMember);
        given(emotionLogRepository.findByIdAndMember(logId, testMember)).willReturn(Optional.of(log));

        // when
        EmotionResponse.Detail detail = emotionService.getEmotionDetail(logId);

        // then
        assertThat(detail).isNotNull();
        assertThat(detail.level()).isEqualTo(EmotionLevel.COMFORTABLE);
        assertThat(detail.memo()).isEqualTo("상세 메모");

        then(emotionLogRepository).should().findByIdAndMember(logId, testMember);
    }

    @Test
    @DisplayName("감정 상세 조회: 존재하지 않는 ID이거나 타 회원의 기록이면 EntityNotFoundException 예외가 발생한다")
    void getEmotionDetail_NotFound() {
        // given
        Long notFoundId = 999L;
        given(memberService.getOrCreateDefaultMember()).willReturn(testMember);
        given(emotionLogRepository.findByIdAndMember(notFoundId, testMember)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> emotionService.getEmotionDetail(notFoundId))
                .isInstanceOf(EntityNotFoundException.class)
                .satisfies(ex -> {
                    EntityNotFoundException enfe = (EntityNotFoundException) ex;
                    assertThat(enfe.getErrorCode()).isEqualTo(ErrorCode.EMOTION_LOG_NOT_FOUND);
                });

        then(emotionLogRepository).should().findByIdAndMember(notFoundId, testMember);
    }

    @Test
    @DisplayName("감정 기록 삭제: 존재하는 기록이면 정상 삭제된다")
    void deleteEmotion_Success() {
        // given
        Long logId = 1L;
        EmotionLog log = EmotionLog.create(testMember, EmotionLevel.COMFORTABLE, "삭제할 메모", LocalDateTime.now());

        given(memberService.getOrCreateDefaultMember()).willReturn(testMember);
        given(emotionLogRepository.findByIdAndMember(logId, testMember)).willReturn(Optional.of(log));

        // when
        emotionService.deleteEmotion(logId);

        // then
        then(emotionLogRepository).should().delete(log);
    }

    @Test
    @DisplayName("감정 기록 삭제: 존재하지 않는 기록이면 EntityNotFoundException 예외가 발생한다")
    void deleteEmotion_NotFound() {
        // given
        Long notFoundId = 999L;
        given(memberService.getOrCreateDefaultMember()).willReturn(testMember);
        given(emotionLogRepository.findByIdAndMember(notFoundId, testMember)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> emotionService.deleteEmotion(notFoundId))
                .isInstanceOf(EntityNotFoundException.class)
                .satisfies(ex -> {
                    EntityNotFoundException enfe = (EntityNotFoundException) ex;
                    assertThat(enfe.getErrorCode()).isEqualTo(ErrorCode.EMOTION_LOG_NOT_FOUND);
                });

        then(emotionLogRepository).should(never()).delete(any());
    }
}
