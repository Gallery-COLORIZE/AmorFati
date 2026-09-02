package com.colorize.amorfati.domain.emotion.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum EmotionLevel {

    DEEP_HEAVY(1, "몹시 지침", "1단계: 몹시 지침/괴로움", "#6D6574", "bg-[#6D6574]", "ring-[#8B8094]/50", "cloud-rain"),
    UNEASY_LOW(2, "불안/답답", "2단계: 불안함/가라앉음", "#5F7184", "bg-[#5F7184]", "ring-[#8094AA]/50", "wind"),
    CALM_NEUTRAL(3, "담담/잔잔", "3단계: 담담함/잔잔함", "#597A6B", "bg-[#597A6B]", "ring-[#7A9E8E]/50", "leaf"),
    COMFORTABLE(4, "편안함", "4단계: 편안함/소소한 온기", "#8F6A55", "bg-[#8F6A55]", "ring-[#B58B74]/50", "heart"),
    BRIGHT_ENERGIZED(5, "가벼움", "5단계: 충만함/가벼움", "#9E7D44", "bg-[#9E7D44]", "ring-[#C7A362]/50", "sparkles");

    private final int score;
    private final String shortLabel;
    private final String title;
    private final String colorHex;
    private final String dotClass;
    private final String ringClass;
    private final String icon;

    public static EmotionLevel fromScore(int score) {
        return Arrays.stream(values())
                .filter(level -> level.score == score)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 감정 레벨 점수입니다: " + score));
    }
}
