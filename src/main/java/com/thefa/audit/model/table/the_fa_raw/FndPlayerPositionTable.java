package com.thefa.audit.model.table.the_fa_raw;

import com.google.cloud.Timestamp;
import com.thefa.audit.model.dto.player.base.PlayerPositionDTO;
import lombok.Data;
import lombok.NonNull;

import java.util.Optional;

@Data
public class FndPlayerPositionTable {

    private String playerId;

    private Integer positionNumber;

    private Integer positionOrder;

    private String lastModified;

    public static FndPlayerPositionTable fromPlayerPositionDTO(@NonNull PlayerPositionDTO playerPositionDTO,
                                                               @NonNull String playerId,
                                                               String lastModified) {

        FndPlayerPositionTable fndPlayerPosition = new FndPlayerPositionTable();

        fndPlayerPosition.playerId = playerId;
        fndPlayerPosition.positionNumber = playerPositionDTO.getPositionNumber();
        fndPlayerPosition.positionOrder = playerPositionDTO.getPositionOrder();
        fndPlayerPosition.lastModified = Optional.ofNullable(lastModified).orElseGet(() -> Timestamp.now().toString());
        return fndPlayerPosition;

    }
}
