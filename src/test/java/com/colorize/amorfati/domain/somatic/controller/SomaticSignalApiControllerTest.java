package com.colorize.amorfati.domain.somatic.controller;

import com.colorize.amorfati.domain.somatic.dto.SomaticSignalResponse;
import com.colorize.amorfati.domain.somatic.service.SomaticSignalService;
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

@WebMvcTest(SomaticSignalApiController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalResponseAdvice.class, GlobalExceptionHandler.class})
class SomaticSignalApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SomaticSignalService somaticSignalService;

    @Test
    @DisplayName("[GET /api/somatic-signals] 신체 반응 프리셋 목록을 정상 조회하고 200 OK를 반환한다")
    void getSomaticSignals_Success() throws Exception {
        // given
        List<SomaticSignalResponse> mockSignals = List.of(
                new SomaticSignalResponse(1L, "가슴 답답함", "heart-crack", 1),
                new SomaticSignalResponse(2L, "호흡이 얕음", "wind", 2)
        );
        given(somaticSignalService.getAllSomaticSignals()).willReturn(mockSignals);

        // when
        ResultActions result = mockMvc.perform(get("/api/somatic-signals"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name").value("가슴 답답함"))
                .andExpect(jsonPath("$.data[0].icon").value("heart-crack"));

        then(somaticSignalService).should().getAllSomaticSignals();
    }
}
