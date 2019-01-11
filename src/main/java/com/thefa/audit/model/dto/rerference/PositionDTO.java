package com.thefa.audit.model.dto.rerference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PositionDTO {

    @EqualsAndHashCode.Include
    private Integer positionNumber;

    private String description;

}
