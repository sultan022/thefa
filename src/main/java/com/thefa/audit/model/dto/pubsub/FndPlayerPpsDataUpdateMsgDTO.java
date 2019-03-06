package com.thefa.audit.model.dto.pubsub;

import com.thefa.audit.model.dto.player.small.PlayerBasicDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FndPlayerPpsDataUpdateMsgDTO {

    private String playerId;
    private PlayerBasicDTO data;
    private ZonedDateTime updatedDate;

}
