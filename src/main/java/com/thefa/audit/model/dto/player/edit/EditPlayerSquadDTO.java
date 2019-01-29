package com.thefa.audit.model.dto.player.edit;

import com.thefa.audit.model.shared.SquadStatusType;
import com.thefa.audit.model.shared.SquadType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EditPlayerSquadDTO {

    @NotNull
    @EqualsAndHashCode.Include
    private SquadType squad;

    private SquadStatusType status;
}
