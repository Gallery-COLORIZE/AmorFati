package com.colorize.amorfati.domain.somatic.service;

import com.colorize.amorfati.domain.somatic.dto.SomaticSignalResponse;
import com.colorize.amorfati.domain.somatic.repository.SomaticSignalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SomaticSignalService {

    private final SomaticSignalRepository somaticSignalRepository;

    public List<SomaticSignalResponse> getAllSomaticSignals() {
        return somaticSignalRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(SomaticSignalResponse::from)
                .toList();
    }
}
