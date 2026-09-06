package com.colorize.amorfati.domain.trigger.repository;

import com.colorize.amorfati.domain.trigger.entity.TriggerFactor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TriggerFactorRepositoryTest {

    @Autowired
    private TriggerFactorRepository triggerFactorRepository;

    @Test
    @DisplayName("findAllByOrderByDisplayOrderAsc: 상황/트리거 목록이 displayOrder 오름차순으로 정렬되어 조회된다")
    void findAllByOrderByDisplayOrderAsc_Success() {
        // given & when
        List<TriggerFactor> triggers = triggerFactorRepository.findAllByOrderByDisplayOrderAsc();

        // then
        assertThat(triggers).hasSizeGreaterThanOrEqualTo(8);
        for (int i = 0; i < triggers.size() - 1; i++) {
            assertThat(triggers.get(i).getDisplayOrder())
                    .isLessThanOrEqualTo(triggers.get(i + 1).getDisplayOrder());
        }
        assertThat(triggers.get(0).getName()).isEqualTo("대인관계/대화");
    }

    @Test
    @DisplayName("findAllByIdIn: 주어진 ID 목록에 해당하는 상황/트리거 요인들만 정확히 조회된다")
    void findAllByIdIn_Success() {
        // given
        List<Long> targetIds = List.of(1L, 4L, 7L);

        // when
        List<TriggerFactor> result = triggerFactorRepository.findAllByIdIn(targetIds);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(TriggerFactor::getId)
                .containsExactlyInAnyOrder(1L, 4L, 7L);
    }

    @Test
    @DisplayName("findAllByIdIn: 비어 있는 ID 목록을 넘기면 빈 결과를 반환한다")
    void findAllByIdIn_EmptyList() {
        // given
        List<Long> emptyIds = List.of();

        // when
        List<TriggerFactor> result = triggerFactorRepository.findAllByIdIn(emptyIds);

        // then
        assertThat(result).isEmpty();
    }
}
