package com.thefa.audit.model.dto.rest.pma;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PmaDate {

    @JsonProperty("IsoDateTime")
    private String isoDate;

    @JsonProperty("CleanIsoDateTime")
    private String cleanIsoDate;

}
