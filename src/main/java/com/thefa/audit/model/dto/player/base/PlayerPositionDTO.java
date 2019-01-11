package com.thefa.audit.model.dto.player.base;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerPositionDTO {

    @EqualsAndHashCode.Include
    @Range(min = 1, max = 11)
    private int positionNumber;

    @Min(0)
    private int positionOrder;

}
