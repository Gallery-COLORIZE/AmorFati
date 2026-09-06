package com.colorize.amorfati.domain.somatic.repository;

import com.colorize.amorfati.domain.somatic.entity.SomaticSignal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SomaticSignalRepositoryTest {

    @Autowired
    private SomaticSignalRepository somaticSignalRepository;

    @Test
    @DisplayName("findAllByOrderByDisplayOrderAsc: 신체 반응 목록이 displayOrder 오름차순으로 정렬되어 조회된다")
    void findAllByOrderByDisplayOrderAsc_Success() {
        // given & when
        List<SomaticSignal> signals = somaticSignalRepository.findAllByOrderByDisplayOrderAsc();

        // then
        assertThat(signals).hasSizeGreaterThanOrEqualTo(9);
        for (int i = 0; i < signals.size() - 1; i++) {
            assertThat(signals.get(i).getDisplayOrder())
                    .isLessThanOrEqualTo(signals.get(i + 1).getDisplayOrder());
        }
        assertThat(signals.get(0).getName()).isEqualTo("가슴 답답함");
    }

    @Test
    @DisplayName("findAllByIdIn: 주어진 ID 목록에 해당하는 신체 반응들만 정확히 조회된다")
    void findAllByIdIn_Success() {
        // given
        List<Long> targetIds = List.of(1L, 2L, 5L);

        // when
        List<SomaticSignal> result = somaticSignalRepository.findAllByIdIn(targetIds);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(SomaticSignal::getId)
                .containsExactlyInAnyOrder(1L, 2L, 5L);
    }

    @Test
    @DisplayName("findAllByIdIn: 비어 있는 ID 목록을 넘기면 빈 결과를 반환한다")
    void findAllByIdIn_EmptyList() {
        // given
        List<Long> emptyIds = List.of();

        // when
        List<SomaticSignal> result = somaticSignalRepository.findAllByIdIn(emptyIds);

        // then
        assertThat(result).isEmpty();
    }
}
