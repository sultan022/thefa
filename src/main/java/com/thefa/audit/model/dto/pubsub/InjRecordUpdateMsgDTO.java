package com.thefa.audit.model.dto.pubsub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InjRecordUpdateMsgDTO {

    private String playerId;
    private String pmaExternalId;

}
