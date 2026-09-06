package com.colorize.amorfati.domain.emotion.controller;

import com.colorize.amorfati.domain.emotion.dto.EmotionRequest;
import com.colorize.amorfati.domain.emotion.dto.EmotionResponse;
import com.colorize.amorfati.domain.emotion.entity.EmotionLevel;
import com.colorize.amorfati.domain.emotion.service.EmotionService;
import com.colorize.amorfati.global.common.GlobalResponseAdvice;
import com.colorize.amorfati.global.error.ErrorCode;
import com.colorize.amorfati.global.error.GlobalExceptionHandler;
import com.colorize.amorfati.global.error.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmotionApiController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalResponseAdvice.class, GlobalExceptionHandler.class})
class EmotionApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmotionService emotionService;

    @Test
    @DisplayName("[POST /api/emotions] 정상 요청 시 감정 기록이 생성되고 201 Created를 반환한다")
    void createEmotion_Success() throws Exception {
        // given
        EmotionRequest.Create request = new EmotionRequest.Create(
                4,
                List.of(8L, 9L),
                List.of(5L, 7L),
                "오랜만에 여유롭게 산책을 다녀왔다.",
                null
        );

        EmotionResponse.TimelineItem responseItem = new EmotionResponse.TimelineItem(
                1L,
                4,
                "4단계: 편안함/소소한 온기",
                "bg-[#8F6A55]",
                "heart",
                List.of(new EmotionResponse.TagItem(8L, "깊은 이완/호흡 편안", "smile")),
                List.of(new EmotionResponse.TagItem(5L, "혼자만의 시간", "coffee")),
                "오랜만에 여유롭게 산책을 다녀왔다.",
                "17:30",
                LocalDateTime.now()
        );

        given(emotionService.recordEmotion(any(EmotionRequest.Create.class))).willReturn(responseItem);

        // when
        ResultActions result = mockMvc.perform(post("/api/emotions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.level").value(4))
                .andExpect(jsonPath("$.data.levelTitle").value("4단계: 편안함/소소한 온기"))
                .andExpect(jsonPath("$.data.memo").value("오랜만에 여유롭게 산책을 다녀왔다."))
                .andExpect(jsonPath("$.data.somaticSignals", hasSize(1)))
                .andExpect(jsonPath("$.data.triggerFactors", hasSize(1)));

        then(emotionService).should().recordEmotion(any(EmotionRequest.Create.class));
    }

    @Test
    @DisplayName("[POST /api/emotions] 감정 레벨 범위(1~5)를 벗어나면 400 Bad Request와 검증 에러를 반환한다")
    void createEmotion_InvalidLevel_BadRequest() throws Exception {
        // given
        EmotionRequest.Create invalidRequest = new EmotionRequest.Create(
                6, // 최대 5 초과
                List.of(),
                List.of(),
                "잘못된 레벨 테스트",
                null
        );

        // when
        ResultActions result = mockMvc.perform(post("/api/emotions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("C001"))
                .andExpect(jsonPath("$.errors[0].field").value("level"));

        then(emotionService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("[GET /api/emotions/today] 오늘 감정 궤적 목록을 정상 조회하고 200 OK를 반환한다")
    void getTodayEmotions_Success() throws Exception {
        // given
        EmotionResponse.TimelineItem item = new EmotionResponse.TimelineItem(
                1L,
                4,
                "4단계: 편안함/소소한 온기",
                "bg-[#8F6A55]",
                "heart",
                List.of(),
                List.of(),
                "오늘 하루 기록",
                "15:00",
                LocalDateTime.now()
        );

        given(emotionService.getTodayTimeline()).willReturn(List.of(item));

        // when
        ResultActions result = mockMvc.perform(get("/api/emotions/today"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].memo").value("오늘 하루 기록"));

        then(emotionService).should().getTodayTimeline();
    }

    @Test
    @DisplayName("[GET /api/emotions/{id}] 존재하는 ID 조회 시 상세 정보와 200 OK를 반환한다")
    void getEmotionDetail_Success() throws Exception {
        // given
        Long id = 1L;
        EmotionResponse.Detail detail = new EmotionResponse.Detail(
                id,
                EmotionLevel.COMFORTABLE,
                4,
                "상세 조회 메모",
                List.of(),
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(emotionService.getEmotionDetail(id)).willReturn(detail);

        // when
        ResultActions result = mockMvc.perform(get("/api/emotions/{id}", id));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.levelScore").value(4))
                .andExpect(jsonPath("$.data.memo").value("상세 조회 메모"));

        then(emotionService).should().getEmotionDetail(id);
    }

    @Test
    @DisplayName("[GET /api/emotions/{id}] 존재하지 않는 ID 조회 시 404 Not Found를 반환한다")
    void getEmotionDetail_NotFound() throws Exception {
        // given
        Long notFoundId = 999L;
        given(emotionService.getEmotionDetail(notFoundId))
                .willThrow(new EntityNotFoundException(ErrorCode.EMOTION_LOG_NOT_FOUND));

        // when
        ResultActions result = mockMvc.perform(get("/api/emotions/{id}", notFoundId));

        // then
        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("E001"))
                .andExpect(jsonPath("$.message").value("해당 감정 기록을 찾을 수 없습니다."));

        then(emotionService).should().getEmotionDetail(notFoundId);
    }

    @Test
    @DisplayName("[DELETE /api/emotions/{id}] 존재하는 ID 삭제 시 204 No Content를 반환한다")
    void deleteEmotion_Success() throws Exception {
        // given
        Long id = 1L;
        willDoNothing().given(emotionService).deleteEmotion(id);

        // when
        ResultActions result = mockMvc.perform(delete("/api/emotions/{id}", id));

        // then
        result.andDo(print())
                .andExpect(status().isNoContent());

        then(emotionService).should().deleteEmotion(id);
    }

    @Test
    @DisplayName("[DELETE /api/emotions/{id}] 존재하지 않는 ID 삭제 시 404 Not Found를 반환한다")
    void deleteEmotion_NotFound() throws Exception {
        // given
        Long notFoundId = 999L;
        willThrow(new EntityNotFoundException(ErrorCode.EMOTION_LOG_NOT_FOUND))
                .given(emotionService).deleteEmotion(notFoundId);

        // when
        ResultActions result = mockMvc.perform(delete("/api/emotions/{id}", notFoundId));

        // then
        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("E001"));

        then(emotionService).should().deleteEmotion(notFoundId);
    }
}
