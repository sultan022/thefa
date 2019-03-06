package com.thefa.audit.model.table.foundation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import com.thefa.audit.model.dto.player.base.PlayerForeignMappingDTO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

@Data
public class MappingPlayerIdTable {

    private String playerId;

    private String foreignPlayerId;

    private String dataProvider;

    @JsonProperty("isActive")
    @Getter(AccessLevel.NONE) // Required for Object Mapper issue
    private boolean isActive;

    private String lastModified;

    public static MappingPlayerIdTable fromPlayerMappingDTO(@NonNull PlayerForeignMappingDTO playerForeignMappingDTO,
                                                            @NonNull String playerId,
                                                            String lastModified) {

        MappingPlayerIdTable mappingPlayerIdTable = new MappingPlayerIdTable();

        mappingPlayerIdTable.playerId = playerId;
        mappingPlayerIdTable.foreignPlayerId = playerForeignMappingDTO.getForeignPlayerId();
        mappingPlayerIdTable.dataProvider = playerForeignMappingDTO.getSource().name();
        mappingPlayerIdTable.isActive = true;
        mappingPlayerIdTable.lastModified = Optional.ofNullable(lastModified).orElseGet(() -> Timestamp.now().toString());

        return mappingPlayerIdTable;

    }
}
