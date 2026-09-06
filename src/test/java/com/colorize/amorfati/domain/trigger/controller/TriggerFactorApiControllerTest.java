package com.colorize.amorfati.domain.trigger.controller;

import com.colorize.amorfati.domain.trigger.dto.TriggerFactorResponse;
import com.colorize.amorfati.domain.trigger.service.TriggerFactorService;
import com.colorize.amorfati.global.common.GlobalResponseAdvice;
import com.colorize.amorfati.global.error.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TriggerFactorApiController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalResponseAdvice.class, GlobalExceptionHandler.class})
class TriggerFactorApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TriggerFactorService triggerFactorService;

    @Test
    @DisplayName("[GET /api/trigger-factors] 상황/트리거 요인 프리셋 목록을 정상 조회하고 200 OK를 반환한다")
    void getTriggerFactors_Success() throws Exception {
        // given
        List<TriggerFactorResponse> mockFactors = List.of(
                new TriggerFactorResponse(1L, "대인관계/대화", "users", 1),
                new TriggerFactorResponse(2L, "소음/외부 자극", "volume-2", 2)
        );
        given(triggerFactorService.getAllTriggerFactors()).willReturn(mockFactors);

        // when
        ResultActions result = mockMvc.perform(get("/api/trigger-factors"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name").value("대인관계/대화"))
                .andExpect(jsonPath("$.data[0].icon").value("users"));

        then(triggerFactorService).should().getAllTriggerFactors();
    }
}
