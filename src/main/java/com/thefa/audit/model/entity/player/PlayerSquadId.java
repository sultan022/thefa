package com.thefa.audit.model.entity.player;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlayerSquadId implements Serializable {

    private String playerId;

    private String squad;

}
