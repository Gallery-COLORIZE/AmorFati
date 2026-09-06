package com.colorize.amorfati.domain.somatic.controller;

import com.colorize.amorfati.domain.somatic.dto.SomaticSignalResponse;
import com.colorize.amorfati.domain.somatic.service.SomaticSignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/somatic-signals")
@RequiredArgsConstructor
public class SomaticSignalApiController {

    private final SomaticSignalService somaticSignalService;

    @GetMapping
    public List<SomaticSignalResponse> getSomaticSignals() {
        return somaticSignalService.getAllSomaticSignals();
    }
}
