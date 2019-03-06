package com.thefa.audit.model.kind;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "PenaltyDetails")
public class PenaltyDetailsKind {

    private String foreignEventId;
    private String foreignPenaltyId;
    private String index;
    private Double y;
    private Double z;
    private Double xgL;
    private Double xgR;
    private String goalZone;
    private String foot;
    private String ballDirection;
    private String distraction;
    private String diveSide;
    private String diveHeight;
    private String diveDirection;
    private Boolean isGoal;
    private Double pressureRating;
    private Boolean isUnderPressure;
    private Integer penaltyMinute;
    private String penaltyTime;
    private Double penaltyTimeStamp;
    private String penaltyPeriod;
    private String foreignTakerPlayerId;
    private String foreignGoalkeeperPlayerId;
    private String foreignCompetitionId;
    private String foreignFixtureId;
    private String competitionName;
    private String seasonName;
    private String takerName;
    private String goalkeeperName;
    private String takerTeamName;
    private String goalkeeperTeamName;

}
