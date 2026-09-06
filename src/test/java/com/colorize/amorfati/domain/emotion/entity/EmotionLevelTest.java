package com.colorize.amorfati.domain.emotion.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmotionLevelTest {

    @ParameterizedTest(name = "점수 {0} 입력 시 {1} 레벨이 반환된다")
    @CsvSource({
            "1, DEEP_HEAVY",
            "2, UNEASY_LOW",
            "3, CALM_NEUTRAL",
            "4, COMFORTABLE",
            "5, BRIGHT_ENERGIZED"
    })
    @DisplayName("1~5 범위의 점수 입력 시 해당하는 EmotionLevel 상수를 반환한다")
    void fromScore_Valid(int score, EmotionLevel expectedLevel) {
        EmotionLevel actual = EmotionLevel.fromScore(score);

        assertThat(actual).isEqualTo(expectedLevel);
        assertThat(actual.getScore()).isEqualTo(score);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 6, 10, 99})
    @DisplayName("1~5 범위를 벗어난 점수 입력 시 IllegalArgumentException이 발생한다")
    void fromScore_Invalid(int invalidScore) {
        assertThatThrownBy(() -> EmotionLevel.fromScore(invalidScore))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 감정 레벨 점수입니다: " + invalidScore);
    }

    @Test
    @DisplayName("모든 EmotionLevel 상수는 필수 UI 메타데이터(라벨, 색상, 아이콘 등)를 정상 보유한다")
    void enumAttributesAreConfigured() {
        for (EmotionLevel level : EmotionLevel.values()) {
            assertThat(level.getShortLabel()).isNotBlank();
            assertThat(level.getTitle()).isNotBlank();
            assertThat(level.getColorHex()).startsWith("#");
            assertThat(level.getDotClass()).isNotBlank();
            assertThat(level.getRingClass()).isNotBlank();
            assertThat(level.getIcon()).isNotBlank();
        }
    }
}
