package com.thefa.audit.model.dto.rerference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class IntelDTO {

    @EqualsAndHashCode.Include
    private String intelType;

    private String description;
}
