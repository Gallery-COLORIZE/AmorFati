package com.colorize.amorfati.domain.emotion.service;

import com.colorize.amorfati.domain.emotion.dto.EmotionRequest;
import com.colorize.amorfati.domain.emotion.dto.EmotionResponse;
import com.colorize.amorfati.domain.emotion.entity.EmotionLevel;
import com.colorize.amorfati.domain.emotion.entity.EmotionLog;
import com.colorize.amorfati.domain.emotion.repository.EmotionLogRepository;
import com.colorize.amorfati.domain.member.entity.Member;
import com.colorize.amorfati.domain.member.service.MemberService;
import com.colorize.amorfati.domain.somatic.entity.SomaticSignal;
import com.colorize.amorfati.domain.somatic.repository.SomaticSignalRepository;
import com.colorize.amorfati.domain.trigger.entity.TriggerFactor;
import com.colorize.amorfati.domain.trigger.repository.TriggerFactorRepository;
import com.colorize.amorfati.global.error.ErrorCode;
import com.colorize.amorfati.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmotionService {

    private final EmotionLogRepository emotionLogRepository;
    private final SomaticSignalRepository somaticSignalRepository;
    private final TriggerFactorRepository triggerFactorRepository;
    private final MemberService memberService;

    @Transactional
    public EmotionResponse.TimelineItem recordEmotion(EmotionRequest.Create request) {
        Member member = memberService.getOrCreateDefaultMember();
        EmotionLevel level = EmotionLevel.fromScore(request.level());

        EmotionLog emotionLog = request.toEntity(member, level);

        if (request.somaticSignalIds() != null && !request.somaticSignalIds().isEmpty()) {
            List<SomaticSignal> somatics = somaticSignalRepository.findAllByIdIn(request.somaticSignalIds());
            somatics.forEach(emotionLog::addSomaticSignal);
        }

        if (request.triggerFactorIds() != null && !request.triggerFactorIds().isEmpty()) {
            List<TriggerFactor> triggers = triggerFactorRepository.findAllByIdIn(request.triggerFactorIds());
            triggers.forEach(emotionLog::addTriggerFactor);
        }

        EmotionLog saved = emotionLogRepository.save(emotionLog);
        return EmotionResponse.TimelineItem.from(saved);
    }

    public List<EmotionResponse.TimelineItem> getTodayTimeline() {
        Member member = memberService.getOrCreateDefaultMember();
        LocalDate today = LocalDate.now();
        List<EmotionLog> logs = emotionLogRepository.findByMemberAndPeriod(
                member,
                today.atStartOfDay(),
                today.atTime(LocalTime.MAX)
        );

        return logs.stream()
                .map(EmotionResponse.TimelineItem::from)
                .toList();
    }

    public EmotionResponse.Detail getEmotionDetail(Long id) {
        Member member = memberService.getOrCreateDefaultMember();
        EmotionLog log = emotionLogRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EMOTION_LOG_NOT_FOUND));

        return EmotionResponse.Detail.from(log);
    }

    @Transactional
    public void deleteEmotion(Long id) {
        Member member = memberService.getOrCreateDefaultMember();
        EmotionLog log = emotionLogRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EMOTION_LOG_NOT_FOUND));

        emotionLogRepository.delete(log);
    }
}
