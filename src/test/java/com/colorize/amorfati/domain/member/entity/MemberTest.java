package com.colorize.amorfati.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    @DisplayName("Member 빌더 생성 시 필드가 정상 설정되며 authProvider가 null이면 'MOCK'으로 기본 설정된다")
    void createMember_DefaultAuthProvider() {
        // given
        String email = "wanderer@amorfati.me";
        String nickname = "방랑자";

        // when
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .authProvider(null)
                .providerId("sub-12345")
                .build();

        // then
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getAuthProvider()).isEqualTo("MOCK");
        assertThat(member.getProviderId()).isEqualTo("sub-12345");
    }

    @Test
    @DisplayName("authProvider를 명시하여 생성하면 지정된 인증 제공자 값이 설정된다")
    void createMember_CustomAuthProvider() {
        // given
        String email = "kakao_user@kakao.com";
        String nickname = "카카오사용자";
        String authProvider = "KAKAO";

        // when
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .authProvider(authProvider)
                .providerId("kakao-999")
                .build();

        // then
        assertThat(member.getAuthProvider()).isEqualTo("KAKAO");
        assertThat(member.getProviderId()).isEqualTo("kakao-999");
    }

    @Test
    @DisplayName("updateNickname 호출 시 회원의 닉네임이 정상 변경된다")
    void updateNickname_Success() {
        // given
        Member member = Member.builder()
                .email("test@amorfati.me")
                .nickname("기존닉네임")
                .build();

        // when
        member.updateNickname("새로운닉네임");

        // then
        assertThat(member.getNickname()).isEqualTo("새로운닉네임");
    }
}
