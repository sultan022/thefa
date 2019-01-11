package com.thefa.audit.model.dto.rerference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SquadDTO {

    @EqualsAndHashCode.Include
    private String squad;

    private String description;

}
