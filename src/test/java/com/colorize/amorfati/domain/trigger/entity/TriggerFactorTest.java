package com.colorize.amorfati.domain.trigger.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TriggerFactorTest {

    @Test
    @DisplayName("TriggerFactor 빌더 생성 시 필드가 정상 설정된다")
    void create_WithBuilder() {
        // given
        String name = "대인관계/대화";
        String icon = "users";
        int displayOrder = 1;

        // when
        TriggerFactor factor = TriggerFactor.builder()
                .name(name)
                .icon(icon)
                .displayOrder(displayOrder)
                .build();

        // then
        assertThat(factor.getName()).isEqualTo(name);
        assertThat(factor.getIcon()).isEqualTo(icon);
        assertThat(factor.getDisplayOrder()).isEqualTo(displayOrder);
    }

    @Test
    @DisplayName("TriggerFactor.of 정적 팩토리 메서드로 객체를 정상 생성한다")
    void create_WithFactoryMethod() {
        // given
        String name = "소음/외부 자극";
        String icon = "volume-2";
        int displayOrder = 2;

        // when
        TriggerFactor factor = TriggerFactor.of(name, icon, displayOrder);

        // then
        assertThat(factor.getName()).isEqualTo(name);
        assertThat(factor.getIcon()).isEqualTo(icon);
        assertThat(factor.getDisplayOrder()).isEqualTo(displayOrder);
    }
}
