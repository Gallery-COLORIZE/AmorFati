package com.colorize.amorfati.domain.emotion.controller;

import com.colorize.amorfati.domain.emotion.dto.EmotionRequest;
import com.colorize.amorfati.domain.emotion.dto.EmotionResponse;
import com.colorize.amorfati.domain.emotion.service.EmotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionApiController {

    private final EmotionService emotionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmotionResponse.TimelineItem create(@Valid @RequestBody EmotionRequest.Create request) {
        return emotionService.recordEmotion(request);
    }

    @GetMapping("/today")
    public List<EmotionResponse.TimelineItem> getTodayEmotions() {
        return emotionService.getTodayTimeline();
    }

    @GetMapping("/{id}")
    public EmotionResponse.Detail getDetail(@PathVariable("id") Long id) {
        return emotionService.getEmotionDetail(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        emotionService.deleteEmotion(id);
    }
}
