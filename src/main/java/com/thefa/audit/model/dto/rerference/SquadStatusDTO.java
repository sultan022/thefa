package com.thefa.audit.model.dto.rerference;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SquadStatusDTO {

    @EqualsAndHashCode.Include
    @NotEmpty
    private String status;

    private String description;

}
