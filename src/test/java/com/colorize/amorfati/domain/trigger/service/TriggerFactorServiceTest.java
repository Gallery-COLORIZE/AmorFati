package com.colorize.amorfati.domain.trigger.service;

import com.colorize.amorfati.domain.trigger.dto.TriggerFactorResponse;
import com.colorize.amorfati.domain.trigger.entity.TriggerFactor;
import com.colorize.amorfati.domain.trigger.repository.TriggerFactorRepository;
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
class TriggerFactorServiceTest {

    @Mock
    private TriggerFactorRepository triggerFactorRepository;

    @InjectMocks
    private TriggerFactorService triggerFactorService;

    @Test
    @DisplayName("getAllTriggerFactors: 레포지토리에서 조회된 상황/트리거 목록을 DTO 목록으로 변환하여 반환한다")
    void getAllTriggerFactors_Success() {
        // given
        TriggerFactor factor1 = TriggerFactor.builder()
                .name("대인관계/대화")
                .icon("users")
                .displayOrder(1)
                .build();
        TriggerFactor factor2 = TriggerFactor.builder()
                .name("소음/외부 자극")
                .icon("volume-2")
                .displayOrder(2)
                .build();

        given(triggerFactorRepository.findAllByOrderByDisplayOrderAsc())
                .willReturn(List.of(factor1, factor2));

        // when
        List<TriggerFactorResponse> responses = triggerFactorService.getAllTriggerFactors();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("대인관계/대화");
        assertThat(responses.get(0).icon()).isEqualTo("users");
        assertThat(responses.get(0).displayOrder()).isEqualTo(1);
        assertThat(responses.get(1).name()).isEqualTo("소음/외부 자극");

        then(triggerFactorRepository).should().findAllByOrderByDisplayOrderAsc();
    }
}
