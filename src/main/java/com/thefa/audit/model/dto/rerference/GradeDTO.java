package com.thefa.audit.model.dto.rerference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GradeDTO {

    @EqualsAndHashCode.Include
    private String grade;

    private String description;

}
