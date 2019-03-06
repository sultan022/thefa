package com.thefa.audit.model.dto.rerference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data @AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CountryDTO {

    @EqualsAndHashCode.Include
    @NotEmpty
    private String countryCode;

    private String countryName;

    private Integer points;

    private Integer rank;
}
