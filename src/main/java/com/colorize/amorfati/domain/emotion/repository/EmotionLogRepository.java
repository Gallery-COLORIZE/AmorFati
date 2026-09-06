package com.colorize.amorfati.domain.emotion.repository;

import com.colorize.amorfati.domain.emotion.entity.EmotionLog;
import com.colorize.amorfati.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmotionLogRepository extends JpaRepository<EmotionLog, Long> {

    @Query("""
            SELECT e FROM EmotionLog e
            WHERE e.member = :member
              AND e.recordedAt BETWEEN :start AND :end
            ORDER BY e.recordedAt DESC
            """)
    List<EmotionLog> findByMemberAndPeriod(
            @Param("member") Member member,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    Optional<EmotionLog> findByIdAndMember(Long id, Member member);
}
