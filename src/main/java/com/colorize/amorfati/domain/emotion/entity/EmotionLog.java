package com.colorize.amorfati.domain.emotion.entity;

import com.colorize.amorfati.domain.member.entity.Member;
import com.colorize.amorfati.domain.somatic.entity.SomaticSignal;
import com.colorize.amorfati.domain.trigger.entity.TriggerFactor;
import com.colorize.amorfati.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EmotionLevel emotionLevel;

    @Column(nullable = false)
    private int levelScore;

    @Column(length = 1000)
    private String memo;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @OneToMany(mappedBy = "emotionLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmotionLogSomatic> somaticSignals = new ArrayList<>();

    @OneToMany(mappedBy = "emotionLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmotionLogTrigger> triggerFactors = new ArrayList<>();

    @Builder
    public EmotionLog(Member member, EmotionLevel emotionLevel, String memo, LocalDateTime recordedAt) {
        this.member = member;
        this.emotionLevel = emotionLevel;
        this.levelScore = emotionLevel != null ? emotionLevel.getScore() : 3;
        this.memo = memo;
        this.recordedAt = (recordedAt == null) ? LocalDateTime.now() : recordedAt;
    }

    public static EmotionLog create(Member member, EmotionLevel emotionLevel, String memo, LocalDateTime recordedAt) {
        return EmotionLog.builder()
                .member(member)
                .emotionLevel(emotionLevel)
                .memo(memo)
                .recordedAt(recordedAt)
                .build();
    }

    public void addSomaticSignal(SomaticSignal somaticSignal) {
        EmotionLogSomatic mapping = EmotionLogSomatic.of(this, somaticSignal);
        this.somaticSignals.add(mapping);
    }

    public void addTriggerFactor(TriggerFactor triggerFactor) {
        EmotionLogTrigger mapping = EmotionLogTrigger.of(this, triggerFactor);
        this.triggerFactors.add(mapping);
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public void changeEmotionLevel(EmotionLevel level) {
        this.emotionLevel = level;
        this.levelScore = level.getScore();
    }
}
