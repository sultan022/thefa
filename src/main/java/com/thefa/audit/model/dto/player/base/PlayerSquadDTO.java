package com.thefa.audit.model.dto.player.base;


import com.thefa.audit.model.shared.SquadType;
import com.thefa.audit.model.shared.SquadStatusType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerSquadDTO {

    @EqualsAndHashCode.Include
    @NotNull
    private SquadType squad;

    private SquadStatusType status;


}
