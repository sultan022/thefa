package com.thefa.audit.model.dto.rerference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CountryDTO {

    @EqualsAndHashCode.Include
    private String countryCode;

    private String countryName;

    private Integer points;

    private Integer rank;
}
