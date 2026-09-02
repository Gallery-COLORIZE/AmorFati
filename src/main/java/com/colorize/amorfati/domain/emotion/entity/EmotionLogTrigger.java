package com.colorize.amorfati.domain.emotion.entity;

import com.colorize.amorfati.domain.trigger.entity.TriggerFactor;
import com.colorize.amorfati.global.common.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionLogTrigger extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_log_id", nullable = false)
    private EmotionLog emotionLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trigger_factor_id", nullable = false)
    private TriggerFactor triggerFactor;

    public EmotionLogTrigger(EmotionLog emotionLog, TriggerFactor triggerFactor) {
        this.emotionLog = emotionLog;
        this.triggerFactor = triggerFactor;
    }

    public static EmotionLogTrigger of(EmotionLog emotionLog, TriggerFactor triggerFactor) {
        return new EmotionLogTrigger(emotionLog, triggerFactor);
    }
}
