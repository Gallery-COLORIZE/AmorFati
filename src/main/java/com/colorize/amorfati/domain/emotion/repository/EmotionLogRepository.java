package com.colorize.amorfati.domain.emotion.repository;

import com.colorize.amorfati.domain.emotion.entity.EmotionLog;
import com.colorize.amorfati.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EmotionLogRepository extends JpaRepository<EmotionLog, Long> {

    List<EmotionLog> findAllByMemberOrderByRecordedAtDesc(Member member);

    List<EmotionLog> findAllByMemberAndRecordedAtBetweenOrderByRecordedAtDesc(
            Member member,
            LocalDateTime start,
            LocalDateTime end
    );
}
