package com.colorize.amorfati.domain.somatic.repository;

import com.colorize.amorfati.domain.somatic.entity.SomaticSignal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SomaticSignalRepository extends JpaRepository<SomaticSignal, Long> {

    List<SomaticSignal> findAllByOrderByDisplayOrderAsc();

    List<SomaticSignal> findAllByIdIn(Collection<Long> ids);
}
