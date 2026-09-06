package com.colorize.amorfati.domain.trigger.dto;

import com.colorize.amorfati.domain.trigger.entity.TriggerFactor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TriggerFactorResponseTest {

    @Test
    @DisplayName("from: TriggerFactor 엔티티를 TriggerFactorResponse DTO로 올바르게 매핑한다")
    void from_Success() {
        // given
        TriggerFactor factor = TriggerFactor.builder()
                .name("업무/과부하")
                .icon("briefcase")
                .displayOrder(3)
                .build();

        // when
        TriggerFactorResponse response = TriggerFactorResponse.from(factor);

        // then
        assertThat(response.name()).isEqualTo("업무/과부하");
        assertThat(response.icon()).isEqualTo("briefcase");
        assertThat(response.displayOrder()).isEqualTo(3);
    }
}
