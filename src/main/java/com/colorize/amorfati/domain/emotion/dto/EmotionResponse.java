package com.colorize.amorfati.domain.emotion.dto;

import com.colorize.amorfati.domain.emotion.entity.EmotionLevel;
import com.colorize.amorfati.domain.emotion.entity.EmotionLog;
import com.colorize.amorfati.domain.somatic.dto.SomaticSignalResponse;
import com.colorize.amorfati.domain.trigger.dto.TriggerFactorResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmotionResponse {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private EmotionResponse() {}

    public record TimelineItem(
            Long id,
            int level,
            String levelTitle,
            String dotClass,
            String levelIcon,
            List<TagItem> somaticSignals,
            List<TagItem> triggerFactors,
            String memo,
            String time,
            LocalDateTime recordedAt
    ) {
        public static TimelineItem from(EmotionLog log) {
            EmotionLevel level = log.getEmotionLevel();
            return new TimelineItem(
                    log.getId(),
                    log.getLevelScore(),
                    level.getTitle(),
                    level.getDotClass(),
                    level.getIcon(),
                    log.getSomaticSignals().stream()
                            .map(s -> new TagItem(s.getSomaticSignal().getId(), s.getSomaticSignal().getName(), s.getSomaticSignal().getIcon()))
                            .toList(),
                    log.getTriggerFactors().stream()
                            .map(t -> new TagItem(t.getTriggerFactor().getId(), t.getTriggerFactor().getName(), t.getTriggerFactor().getIcon()))
                            .toList(),
                    log.getMemo() != null ? log.getMemo() : "",
                    log.getRecordedAt().format(TIME_FORMATTER),
                    log.getRecordedAt()
            );
        }
    }

    public record Detail(
            Long id,
            EmotionLevel level,
            int levelScore,
            String memo,
            List<SomaticSignalResponse> somaticSignals,
            List<TriggerFactorResponse> triggerFactors,
            LocalDateTime recordedAt,
            LocalDateTime createdAt
    ) {
        public static Detail from(EmotionLog log) {
            return new Detail(
                    log.getId(),
                    log.getEmotionLevel(),
                    log.getLevelScore(),
                    log.getMemo(),
                    log.getSomaticSignals().stream()
                            .map(s -> SomaticSignalResponse.from(s.getSomaticSignal()))
                            .toList(),
                    log.getTriggerFactors().stream()
                            .map(t -> TriggerFactorResponse.from(t.getTriggerFactor()))
                            .toList(),
                    log.getRecordedAt(),
                    log.getCreatedAt()
            );
        }
    }

    public record TagItem(Long id, String name, String icon) {}
}
