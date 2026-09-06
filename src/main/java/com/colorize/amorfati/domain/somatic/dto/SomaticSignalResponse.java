package com.colorize.amorfati.domain.somatic.dto;

import com.colorize.amorfati.domain.somatic.entity.SomaticSignal;

public record SomaticSignalResponse(
        Long id,
        String name,
        String icon,
        int displayOrder
) {
    public static SomaticSignalResponse from(SomaticSignal entity) {
        return new SomaticSignalResponse(
                entity.getId(),
                entity.getName(),
                entity.getIcon(),
                entity.getDisplayOrder()
        );
    }
}
