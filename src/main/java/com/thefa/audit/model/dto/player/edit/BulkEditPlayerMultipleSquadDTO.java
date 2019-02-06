package com.thefa.audit.model.dto.player.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BulkEditPlayerMultipleSquadDTO {

    @NotNull
    @EqualsAndHashCode.Include
    private String playerId;

    @Valid
    private Set<EditPlayerSquadDTO> squads;

}
