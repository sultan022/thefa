package com.thefa.audit.model.table.the_fa_raw;

import com.google.cloud.Timestamp;
import com.thefa.audit.model.dto.player.base.PlayerSquadDTO;
import com.thefa.audit.model.entity.reference.SquadStatus;
import com.thefa.audit.model.shared.SquadStatusType;
import com.thefa.common.dto.shared.SquadType;
import lombok.Data;
import lombok.NonNull;

import java.util.Optional;

@Data
public class FndPlayerSquadTable {

    private String playerId;

    private String squad;

    private String squadStatus;

    private String lastModified;

    public static FndPlayerSquadTable fromPlayerSquadDTO(@NonNull PlayerSquadDTO playerSquadDTO,
                                                         @NonNull String playerId,
                                                         String lastModified) {

        FndPlayerSquadTable fndPlayerSquad = new FndPlayerSquadTable();

        fndPlayerSquad.playerId = playerId;
        fndPlayerSquad.squad = Optional.ofNullable(playerSquadDTO.getSquadType()).map(SquadType::name).orElse(null);
        fndPlayerSquad.squadStatus = Optional.ofNullable(playerSquadDTO.getStatusType()).map(SquadStatusType::name).orElse(null);
        fndPlayerSquad.lastModified = Optional.ofNullable(lastModified).orElseGet(() -> Timestamp.now().toString());
        return fndPlayerSquad;

    }
}
