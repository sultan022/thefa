package com.thefa.audit.model.dto.rerference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data @AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SquadDTO {

    @EqualsAndHashCode.Include
    @NotEmpty
    private String squad;

    private Integer order;

    private String description;

}
