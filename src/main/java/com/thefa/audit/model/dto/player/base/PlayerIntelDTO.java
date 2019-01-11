package com.thefa.audit.model.dto.player.base;

import com.thefa.audit.model.shared.IntelType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerIntelDTO {

    @EqualsAndHashCode.Include
    private Long id;

    private IntelType intelType;

    private String note;

    private boolean archived;

    private String createdBy;

    private ZonedDateTime createdAt;

    private String updatedBy;

    private ZonedDateTime updatedAt;


}
