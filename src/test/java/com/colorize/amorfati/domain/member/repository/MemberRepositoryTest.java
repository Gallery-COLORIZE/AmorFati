package com.colorize.amorfati.domain.member.repository;

import com.colorize.amorfati.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("findByEmail: 등록된 이메일로 회원을 정상 조회한다")
    void findByEmail_Success() {
        // given
        String email = "findme@amorfati.me";
        Member member = memberRepository.save(Member.builder()
                .email(email)
                .nickname("조회테스트")
                .authProvider("LOCAL")
                .build());

        // when
        Optional<Member> found = memberRepository.findByEmail(email);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(member.getId());
        assertThat(found.get().getEmail()).isEqualTo(email);
        assertThat(found.get().getNickname()).isEqualTo("조회테스트");
    }

    @Test
    @DisplayName("findByEmail: 존재하지 않는 이메일로 조회 시 Optional.empty()를 반환한다")
    void findByEmail_NotFound() {
        // given
        String nonExistentEmail = "ghost@amorfati.me";

        // when
        Optional<Member> found = memberRepository.findByEmail(nonExistentEmail);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("이메일 유니크 제약조건: 동일한 이메일로 중복 저장 시 DataIntegrityViolationException이 발생한다")
    void save_DuplicateEmail_ThrowsException() {
        // given
        String duplicateEmail = "duplicate@amorfati.me";
        memberRepository.save(Member.builder()
                .email(duplicateEmail)
                .nickname("회원1")
                .authProvider("LOCAL")
                .build());
        entityManager.flush();

        // when & then
        Member duplicateMember = Member.builder()
                .email(duplicateEmail)
                .nickname("회원2")
                .authProvider("LOCAL")
                .build();

        assertThatThrownBy(() -> {
            memberRepository.save(duplicateMember);
            entityManager.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
