package com.thefa.audit.model.kind;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "PlayerObservation")
public class PlayerObservationKind {

    @Id
    private String id;
    private String observationId;
    private String playerId;
    private String playerName;
    private String playersClub;
    private String position;
    private String reportDate;
    private String scoutType;
    private String scout;
    private String awayClub;
    private String gameScore;
    private String homeClub;

}
