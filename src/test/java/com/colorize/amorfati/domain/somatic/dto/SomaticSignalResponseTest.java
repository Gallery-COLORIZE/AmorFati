package com.colorize.amorfati.domain.somatic.dto;

import com.colorize.amorfati.domain.somatic.entity.SomaticSignal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SomaticSignalResponseTest {

    @Test
    @DisplayName("from: SomaticSignal 엔티티를 SomaticSignalResponse DTO로 올바르게 매핑한다")
    void from_Success() {
        // given
        SomaticSignal signal = SomaticSignal.builder()
                .name("두통/머리 무거움")
                .icon("zap-off")
                .displayOrder(3)
                .build();

        // when
        SomaticSignalResponse response = SomaticSignalResponse.from(signal);

        // then
        assertThat(response.name()).isEqualTo("두통/머리 무거움");
        assertThat(response.icon()).isEqualTo("zap-off");
        assertThat(response.displayOrder()).isEqualTo(3);
    }
}
