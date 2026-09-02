package com.colorize.amorfati.domain.somatic.entity;

import com.colorize.amorfati.global.common.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SomaticSignal extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 50)
    private String icon;

    @Column(nullable = false)
    private int displayOrder;

    @Builder
    public SomaticSignal(String name, String icon, int displayOrder) {
        this.name = name;
        this.icon = icon;
        this.displayOrder = displayOrder;
    }

    public static SomaticSignal of(String name, String icon, int displayOrder) {
        return SomaticSignal.builder()
                .name(name)
                .icon(icon)
                .displayOrder(displayOrder)
                .build();
    }
}
