package com.thefa.audit.model.dto.rerference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DataSourceDTO {

    @EqualsAndHashCode.Include
    private String source;

    private String description;

}
