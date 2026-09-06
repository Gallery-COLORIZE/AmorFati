package com.colorize.amorfati.domain.trigger.service;

import com.colorize.amorfati.domain.trigger.dto.TriggerFactorResponse;
import com.colorize.amorfati.domain.trigger.repository.TriggerFactorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TriggerFactorService {

    private final TriggerFactorRepository triggerFactorRepository;

    public List<TriggerFactorResponse> getAllTriggerFactors() {
        return triggerFactorRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(TriggerFactorResponse::from)
                .toList();
    }
}
