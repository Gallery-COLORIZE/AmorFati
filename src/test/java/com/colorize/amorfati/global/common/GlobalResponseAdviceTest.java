package com.colorize.amorfati.global.common;

import com.colorize.amorfati.global.error.ErrorCode;
import com.colorize.amorfati.global.error.exception.BusinessException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(GlobalResponseAdviceTest.TestApiController.class)
class GlobalResponseAdviceTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @RestController
    @RequestMapping("/test/api")
    static class TestApiController {

        // 1. 순수 DTO(Record) 반환 -> ApiResponse 자동 래핑 검증
        @GetMapping("/record")
        public TestDto getRecord() {
            return new TestDto("AmorFati", 3);
        }

        // 2. 컬렉션 반환 -> ApiResponse.data 배열 자동 래핑 검증
        @GetMapping("/list")
        public List<String> getList() {
            return List.of("신체반응1", "신체반응2");
        }

        // 3. String 반환 -> ClassCastException 없이 JSON ApiResponse 래핑 검증
        @GetMapping("/string")
        public String getString() {
            return "simple-string";
        }

        // 4. 이미 ApiResponse로 래핑된 경우 -> 이중 래핑 방지 검증
        @GetMapping("/already-wrapped")
        public ApiResponse<String> getAlreadyWrapped() {
            return ApiResponse.success("직접 래핑 메시지", "custom-data");
        }

        // 5. BusinessException 발생 -> GlobalExceptionHandler 규격 에러 응답 검증
        @GetMapping("/business-error")
        public void throwBusinessError() {
            throw new BusinessException(ErrorCode.EMOTION_LOG_NOT_FOUND);
        }

        // 6. Validation 에러 (@Valid) -> 400 Bad Request 및 필드 에러 반환 검증
        @PostMapping("/validation")
        public TestValidationDto testValidation(@Valid @RequestBody TestValidationDto request) {
            return request;
        }
    }

    record TestDto(String name, int score) {}
    record TestValidationDto(@NotBlank(message = "이름은 비어있을 수 없습니다.") String name) {}

    @Test
    @DisplayName("순수 Record/DTO 반환 시 ApiResponse 공통 포맷으로 자동 래핑된다")
    void wrapDtoResponse() throws Exception {
        mockMvc.perform(get("/test/api/record"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."))
                .andExpect(jsonPath("$.data.name").value("AmorFati"))
                .andExpect(jsonPath("$.data.score").value(3));
    }

    @Test
    @DisplayName("List 컬렉션 반환 시 ApiResponse.data 배열로 자동 래핑된다")
    void wrapListResponse() throws Exception {
        mockMvc.perform(get("/test/api/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0]").value("신체반응1"))
                .andExpect(jsonPath("$.data[1]").value("신체반응2"));
    }

    @Test
    @DisplayName("String 반환 시 ClassCastException 없이 정상적으로 JSON ApiResponse로 래핑된다")
    void wrapStringResponse() throws Exception {
        mockMvc.perform(get("/test/api/string"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("simple-string"));
    }

    @Test
    @DisplayName("이미 ApiResponse로 감싸진 경우 이중 래핑되지 않는다")
    void doNotDoubleWrap() throws Exception {
        mockMvc.perform(get("/test/api/already-wrapped"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("직접 래핑 메시지"))
                .andExpect(jsonPath("$.data").value("custom-data"));
    }

    @Test
    @DisplayName("BusinessException 발생 시 GlobalExceptionHandler가 규격화된 에러 응답을 반환한다")
    void handleBusinessException() throws Exception {
        mockMvc.perform(get("/test/api/business-error"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("E001"))
                .andExpect(jsonPath("$.message").value("해당 감정 기록을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("@Valid 유효성 검증 실패 시 400 Bad Request와 상세 필드 에러 목록을 반환한다")
    void handleValidationException() throws Exception {
        mockMvc.perform(post("/test/api/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("C001"))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].reason").value("이름은 비어있을 수 없습니다."));
    }

    @Test
    @DisplayName("Thymeleaf View Controller(/)는 ApiResponse 래핑 대상에서 제외되어 정상 뷰(index)를 반환한다")
    void viewRenderingExcluded() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}
