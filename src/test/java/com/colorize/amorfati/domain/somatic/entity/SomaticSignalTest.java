package com.colorize.amorfati.domain.somatic.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SomaticSignalTest {

    @Test
    @DisplayName("SomaticSignal 빌더 생성 시 필드가 정상 설정된다")
    void create_WithBuilder() {
        // given
        String name = "가슴 답답함";
        String icon = "heart-crack";
        int displayOrder = 1;

        // when
        SomaticSignal signal = SomaticSignal.builder()
                .name(name)
                .icon(icon)
                .displayOrder(displayOrder)
                .build();

        // then
        assertThat(signal.getName()).isEqualTo(name);
        assertThat(signal.getIcon()).isEqualTo(icon);
        assertThat(signal.getDisplayOrder()).isEqualTo(displayOrder);
    }

    @Test
    @DisplayName("SomaticSignal.of 정적 팩토리 메서드로 객체를 정상 생성한다")
    void create_WithFactoryMethod() {
        // given
        String name = "호흡이 얕음";
        String icon = "wind";
        int displayOrder = 2;

        // when
        SomaticSignal signal = SomaticSignal.of(name, icon, displayOrder);

        // then
        assertThat(signal.getName()).isEqualTo(name);
        assertThat(signal.getIcon()).isEqualTo(icon);
        assertThat(signal.getDisplayOrder()).isEqualTo(displayOrder);
    }
}
