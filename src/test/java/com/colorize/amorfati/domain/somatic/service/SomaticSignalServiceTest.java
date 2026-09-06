package com.colorize.amorfati.domain.somatic.service;

import com.colorize.amorfati.domain.somatic.dto.SomaticSignalResponse;
import com.colorize.amorfati.domain.somatic.entity.SomaticSignal;
import com.colorize.amorfati.domain.somatic.repository.SomaticSignalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SomaticSignalServiceTest {

    @Mock
    private SomaticSignalRepository somaticSignalRepository;

    @InjectMocks
    private SomaticSignalService somaticSignalService;

    @Test
    @DisplayName("getAllSomaticSignals: 레포지토리에서 조회된 신체 반응 목록을 DTO 목록으로 변환하여 반환한다")
    void getAllSomaticSignals_Success() {
        // given
        SomaticSignal signal1 = SomaticSignal.builder()
                .name("가슴 답답함")
                .icon("heart-crack")
                .displayOrder(1)
                .build();
        SomaticSignal signal2 = SomaticSignal.builder()
                .name("호흡이 얕음")
                .icon("wind")
                .displayOrder(2)
                .build();

        given(somaticSignalRepository.findAllByOrderByDisplayOrderAsc())
                .willReturn(List.of(signal1, signal2));

        // when
        List<SomaticSignalResponse> responses = somaticSignalService.getAllSomaticSignals();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("가슴 답답함");
        assertThat(responses.get(0).icon()).isEqualTo("heart-crack");
        assertThat(responses.get(0).displayOrder()).isEqualTo(1);
        assertThat(responses.get(1).name()).isEqualTo("호흡이 얕음");

        then(somaticSignalRepository).should().findAllByOrderByDisplayOrderAsc();
    }
}
