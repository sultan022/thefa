package com.thefa.audit.model.dto.player.specific;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thefa.audit.model.dto.rerference.SquadDTO;
import com.thefa.audit.model.dto.rerference.SquadStatusDTO;
import com.thefa.audit.model.shared.SquadStatusType;
import com.thefa.common.dto.shared.SquadType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CompletePlayerSquadDTO {

    @EqualsAndHashCode.Include
    private String playerId;

    @EqualsAndHashCode.Include
    @NotNull
    @Valid
    private SquadDTO squad;

    @Valid
    private SquadStatusDTO status;

    @JsonIgnore
    public SquadStatusType getStatusType() {
        return Optional.ofNullable(status)
                .map(SquadStatusDTO::getStatus)
                .map(SquadStatusType::valueOf)
                .orElse(null);
    }

    @JsonIgnore
    public SquadType getSquadType() {
        return Optional.ofNullable(squad)
                .map(SquadDTO::getSquad)
                .map(SquadType::valueOf)
                .orElse(null);
    }


}
