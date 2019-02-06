package com.thefa.audit.model.dto.player.edit;

import com.thefa.audit.model.shared.SquadStatusType;
import com.thefa.audit.model.shared.SquadType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class BulkEditPlayerSingleSquadDTO {

    @NotEmpty
    private Set<String> playerIds;

    @NotNull
    private SquadType fromSquad;

    @NotNull
    private SquadType toSquad;

    private SquadStatusType toStatus;
}
