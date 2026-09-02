package com.colorize.amorfati.domain.member.entity;

import com.colorize.amorfati.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String authProvider;

    @Column(length = 100)
    private String providerId;

    @Builder
    public Member(String email, String nickname, String authProvider, String providerId) {
        this.email = email;
        this.nickname = nickname;
        this.authProvider = (authProvider == null) ? "MOCK" : authProvider;
        this.providerId = providerId;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
