package com.colorize.amorfati.domain.trigger.controller;

import com.colorize.amorfati.domain.trigger.dto.TriggerFactorResponse;
import com.colorize.amorfati.domain.trigger.service.TriggerFactorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trigger-factors")
@RequiredArgsConstructor
public class TriggerFactorApiController {

    private final TriggerFactorService triggerFactorService;

    @GetMapping
    public List<TriggerFactorResponse> getTriggerFactors() {
        return triggerFactorService.getAllTriggerFactors();
    }
}
