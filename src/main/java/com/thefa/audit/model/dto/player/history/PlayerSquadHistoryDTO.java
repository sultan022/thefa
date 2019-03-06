package com.thefa.audit.model.dto.player.history;


import com.thefa.audit.model.shared.Assignment;
import com.thefa.audit.model.shared.SquadStatusType;
import com.thefa.common.dto.shared.SquadType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode
public class PlayerSquadHistoryDTO {

    private String playerId;

    private SquadType squad;

    private SquadStatusType status;

    private Assignment assignment;

    private ZonedDateTime createdAt;

    private String createdBy;

}