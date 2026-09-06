package com.colorize.amorfati.domain.emotion.dto;

import com.colorize.amorfati.domain.emotion.entity.EmotionLevel;
import com.colorize.amorfati.domain.emotion.entity.EmotionLog;
import com.colorize.amorfati.domain.member.entity.Member;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class EmotionRequest {

    private EmotionRequest() {}

    public record Create(
            @NotNull(message = "감정 레벨은 필수입니다.")
            @Min(value = 1, message = "감정 레벨은 1 이상이어야 합니다.")
            @Max(value = 5, message = "감정 레벨은 5 이하여야 합니다.")
            Integer level,

            List<Long> somaticSignalIds,
            List<Long> triggerFactorIds,

            @Size(max = 1000, message = "메모는 최대 1000자까지 작성할 수 있습니다.")
            String memo,

            LocalDateTime recordedAt
    ) {
        public Create {
            level = (level == null) ? 3 : level;
            somaticSignalIds = (somaticSignalIds == null) ? List.of() : List.copyOf(somaticSignalIds);
            triggerFactorIds = (triggerFactorIds == null) ? List.of() : List.copyOf(triggerFactorIds);
            recordedAt = (recordedAt == null) ? LocalDateTime.now() : recordedAt;
        }

        public EmotionLog toEntity(Member member, EmotionLevel emotionLevel) {
            return EmotionLog.builder()
                    .member(member)
                    .emotionLevel(emotionLevel)
                    .memo(memo)
                    .recordedAt(recordedAt)
                    .build();
        }
    }

    public record Update(
            @Min(value = 1, message = "감정 레벨은 1 이상이어야 합니다.")
            @Max(value = 5, message = "감정 레벨은 5 이하여야 합니다.")
            Integer level,

            List<Long> somaticSignalIds,
            List<Long> triggerFactorIds,

            @Size(max = 1000, message = "메모는 최대 1000자까지 작성할 수 있습니다.")
            String memo
    ) {
        public Update {
            somaticSignalIds = (somaticSignalIds == null) ? List.of() : List.copyOf(somaticSignalIds);
            triggerFactorIds = (triggerFactorIds == null) ? List.of() : List.copyOf(triggerFactorIds);
        }
    }
}
