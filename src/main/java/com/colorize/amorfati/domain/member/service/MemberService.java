package com.colorize.amorfati.domain.member.service;

import com.colorize.amorfati.domain.member.entity.Member;
import com.colorize.amorfati.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    public static final String DEFAULT_GUEST_EMAIL = "guest@amorfati.me";

    private final MemberRepository memberRepository;

    @Transactional
    public Member getOrCreateDefaultMember() {
        return memberRepository.findByEmail(DEFAULT_GUEST_EMAIL)
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .email(DEFAULT_GUEST_EMAIL)
                                .nickname("방랑자")
                                .authProvider("MOCK")
                                .providerId(null)
                                .build()
                ));
    }
}
