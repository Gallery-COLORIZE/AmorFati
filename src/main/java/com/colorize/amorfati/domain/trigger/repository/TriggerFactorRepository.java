package com.colorize.amorfati.domain.trigger.repository;

import com.colorize.amorfati.domain.trigger.entity.TriggerFactor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TriggerFactorRepository extends JpaRepository<TriggerFactor, Long> {

    List<TriggerFactor> findAllByOrderByDisplayOrderAsc();

    List<TriggerFactor> findAllByIdIn(Collection<Long> ids);
}
