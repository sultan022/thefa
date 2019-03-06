package com.thefa.audit.model.dto.pubsub;

import com.thefa.audit.model.dto.player.base.PlayerForeignMappingDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FndPlayerMappingsUpdateMsgDTO {

    private String playerId;
    private Set<PlayerForeignMappingDTO> mappings = new HashSet<>();
    private ZonedDateTime updatedDate;

}
