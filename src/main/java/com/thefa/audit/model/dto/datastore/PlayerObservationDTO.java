package com.thefa.audit.model.dto.datastore;

import lombok.Data;

@Data
public class PlayerObservationDTO {

    private String playerId;
    private String playerName;
    private String playersClub;
    private String position;
    private String reportDate;
    private String scoutType;
    private String scout;
    private String awayClub	;
    private String gameScore;
    private String homeClub;

}
