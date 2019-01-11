package com.thefa.audit.model.entity.player;

import com.thefa.audit.model.shared.SquadType;
import lombok.Data;

import java.io.Serializable;

@Data
public class PlayerSquadId implements Serializable {

    private Long fanId;

    private SquadType squad;


}
