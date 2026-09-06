package com.colorize.amorfati.domain.emotion.repository;

import com.colorize.amorfati.domain.emotion.entity.EmotionLevel;
import com.colorize.amorfati.domain.emotion.entity.EmotionLog;
import com.colorize.amorfati.domain.member.entity.Member;
import com.colorize.amorfati.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class EmotionLogRepositoryTest {

    @Autowired
    private EmotionLogRepository emotionLogRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member memberA;
    private Member memberB;

    @BeforeEach
    void setUp() {
        memberA = memberRepository.save(Member.builder()
                .email("memberA@colorize.com")
                .nickname("회원A")
                .authProvider("LOCAL")
                .build());

        memberB = memberRepository.save(Member.builder()
                .email("memberB@colorize.com")
                .nickname("회원B")
                .authProvider("LOCAL")
                .build());
    }

    @Test
    @DisplayName("findByMemberAndPeriod: 특정 회원의 지정된 기간 내 기록만 최신순으로 조회된다")
    void findByMemberAndPeriod_Success() {
        // given
        LocalDateTime baseTime = LocalDateTime.of(2026, 9, 6, 12, 0);

        // 회원A 기록: 어제 1건, 오늘 오전 1건, 오늘 오후 1건
        EmotionLog yesterday = emotionLogRepository.save(EmotionLog.create(
                memberA, EmotionLevel.DEEP_HEAVY, "어제 기록", baseTime.minusDays(1)
        ));
        EmotionLog todayMorning = emotionLogRepository.save(EmotionLog.create(
                memberA, EmotionLevel.CALM_NEUTRAL, "오늘 오전 기록", baseTime.minusHours(3)
        ));
        EmotionLog todayAfternoon = emotionLogRepository.save(EmotionLog.create(
                memberA, EmotionLevel.COMFORTABLE, "오늘 오후 기록", baseTime.plusHours(2)
        ));

        // 회원B 기록: 오늘 오후 1건 (다른 회원의 기록)
        emotionLogRepository.save(EmotionLog.create(
                memberB, EmotionLevel.BRIGHT_ENERGIZED, "회원B 기록", baseTime
        ));

        LocalDateTime start = baseTime.toLocalDate().atStartOfDay();
        LocalDateTime end = baseTime.toLocalDate().atTime(23, 59, 59);

        // when
        List<EmotionLog> result = emotionLogRepository.findByMemberAndPeriod(memberA, start, end);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(todayAfternoon.getId());
        assertThat(result.get(0).getMemo()).isEqualTo("오늘 오후 기록");
        assertThat(result.get(1).getId()).isEqualTo(todayMorning.getId());
        assertThat(result.get(1).getMemo()).isEqualTo("오늘 오전 기록");
    }

    @Test
    @DisplayName("findByIdAndMember: 본인의 감정 기록은 정상 조회되지만 타 회원의 감정 기록은 조회되지 않는다")
    void findByIdAndMember() {
        // given
        EmotionLog savedLog = emotionLogRepository.save(EmotionLog.create(
                memberA, EmotionLevel.COMFORTABLE, "회원A의 메모", LocalDateTime.now()
        ));

        // when & then: 1. 본인 조회 -> 성공
        Optional<EmotionLog> ownLog = emotionLogRepository.findByIdAndMember(savedLog.getId(), memberA);
        assertThat(ownLog).isPresent();
        assertThat(ownLog.get().getMemo()).isEqualTo("회원A의 메모");

        // when & then: 2. 타 회원(회원B)으로 조회 -> empty
        Optional<EmotionLog> otherLog = emotionLogRepository.findByIdAndMember(savedLog.getId(), memberB);
        assertThat(otherLog).isEmpty();

        // when & then: 3. 존재하지 않는 ID 조회 -> empty
        Optional<EmotionLog> notFoundLog = emotionLogRepository.findByIdAndMember(99999L, memberA);
        assertThat(notFoundLog).isEmpty();
    }
}
