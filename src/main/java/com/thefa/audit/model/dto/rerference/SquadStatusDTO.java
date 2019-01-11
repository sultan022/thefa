package com.thefa.audit.model.dto.rerference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SquadStatusDTO {

    @EqualsAndHashCode.Include
    private String status;

    private String description;

}
