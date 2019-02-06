package com.thefa.audit.model.dto.pubsub;

import com.thefa.audit.model.dto.player.base.PlayerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FndRecordUpdateMsgDTO {

    private String playerId;
    private PlayerDTO data;
    private ZonedDateTime updatedDate;

}
