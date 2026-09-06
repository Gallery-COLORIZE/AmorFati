package com.colorize.amorfati.domain.emotion.dto;

import com.colorize.amorfati.domain.emotion.entity.EmotionLevel;

public record EmotionLevelResponse(
        int level,
        String code,
        String shortLabel,
        String title,
        String colorHex,
        String dotClass,
        String ringClass,
        String icon
) {
    public static EmotionLevelResponse from(EmotionLevel level) {
        return new EmotionLevelResponse(
                level.getScore(),
                level.name(),
                level.getShortLabel(),
                level.getTitle(),
                level.getColorHex(),
                level.getDotClass(),
                level.getRingClass(),
                level.getIcon()
        );
    }
}
