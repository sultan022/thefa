package com.thefa.audit.model.dto.rest.pma;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor @NoArgsConstructor
public class PmaPlayerInjuryTeamDTO {

    @JsonProperty("Teams")
    List<Integer> teamIds;
}
