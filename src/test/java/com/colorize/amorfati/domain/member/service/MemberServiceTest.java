package com.colorize.amorfati.domain.member.service;

import com.colorize.amorfati.domain.member.entity.Member;
import com.colorize.amorfati.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("getOrCreateDefaultMember: 기본 게스트 회원이 이미 DB에 존재하면 새로 저장하지 않고 기존 회원을 반환한다")
    void getOrCreateDefaultMember_WhenAlreadyExists() {
        // given
        Member existingMember = Member.builder()
                .email(MemberService.DEFAULT_GUEST_EMAIL)
                .nickname("기존방랑자")
                .authProvider("MOCK")
                .build();

        given(memberRepository.findByEmail(MemberService.DEFAULT_GUEST_EMAIL))
                .willReturn(Optional.of(existingMember));

        // when
        Member result = memberService.getOrCreateDefaultMember();

        // then
        assertThat(result).isEqualTo(existingMember);
        assertThat(result.getEmail()).isEqualTo(MemberService.DEFAULT_GUEST_EMAIL);
        assertThat(result.getNickname()).isEqualTo("기존방랑자");

        then(memberRepository).should().findByEmail(MemberService.DEFAULT_GUEST_EMAIL);
        then(memberRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("getOrCreateDefaultMember: 기본 게스트 회원이 DB에 없으면 신규 회원을 생성하여 저장 후 반환한다")
    void getOrCreateDefaultMember_WhenNotExists() {
        // given
        given(memberRepository.findByEmail(MemberService.DEFAULT_GUEST_EMAIL))
                .willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        Member result = memberService.getOrCreateDefaultMember();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(MemberService.DEFAULT_GUEST_EMAIL);
        assertThat(result.getNickname()).isEqualTo("방랑자");
        assertThat(result.getAuthProvider()).isEqualTo("MOCK");

        then(memberRepository).should().findByEmail(MemberService.DEFAULT_GUEST_EMAIL);
        then(memberRepository).should().save(any(Member.class));
    }
}
