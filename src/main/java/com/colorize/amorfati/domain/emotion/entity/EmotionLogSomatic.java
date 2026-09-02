package com.colorize.amorfati.domain.emotion.entity;

import com.colorize.amorfati.domain.somatic.entity.SomaticSignal;
import com.colorize.amorfati.global.common.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionLogSomatic extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_log_id", nullable = false)
    private EmotionLog emotionLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "somatic_signal_id", nullable = false)
    private SomaticSignal somaticSignal;

    public EmotionLogSomatic(EmotionLog emotionLog, SomaticSignal somaticSignal) {
        this.emotionLog = emotionLog;
        this.somaticSignal = somaticSignal;
    }

    public static EmotionLogSomatic of(EmotionLog emotionLog, SomaticSignal somaticSignal) {
        return new EmotionLogSomatic(emotionLog, somaticSignal);
    }
}
