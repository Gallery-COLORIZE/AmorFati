package com.colorize.amorfati.domain.trigger.dto;

import com.colorize.amorfati.domain.trigger.entity.TriggerFactor;

public record TriggerFactorResponse(
        Long id,
        String name,
        String icon,
        int displayOrder
) {
    public static TriggerFactorResponse from(TriggerFactor entity) {
        return new TriggerFactorResponse(
                entity.getId(),
                entity.getName(),
                entity.getIcon(),
                entity.getDisplayOrder()
        );
    }
}
