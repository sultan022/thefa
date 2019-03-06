package com.thefa.audit.model.kind;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "SportsCode")  //kind name yet to confirm
public class PlayerSportsCodeKind {

    @Id
    private String id;
    private String fixtureId;
    private String fileName;
    private String date;
    private String team;
    private String playerId;
    private String comp;
    private String homeTeam;
    private String homeScore;
    private String awayTeam;
    private String awayScore;
    private Double startTime;
    private Double endTime;
    private String code;
    private Integer position;
    private String competitionId;
    private String competitionType;
    private String teamType;
    private String seasonId;

    private Integer actionP90;
    private Integer inPossession;
    private Integer bigSaveP90;
    private Integer dangPosession;
    private Integer completition;
    private Integer defendingWonOppDuel;
    private Integer defendingWonDuel;
    private Integer posessionLostOrRetained;
    private Integer competition;
    private Integer chance;
    private Integer crosses;
    private Integer pressAttempt;
    private Integer defensiveDuel;
    private Integer defensiveAerialDuel;
    private Integer defensiveAerialOutcome;
    private Integer interception;
    private Integer ballRecovery;
    private Integer interceptionAndBallRecovery;
    private Integer penaltyBoxClearance;
    private Integer keyDefensiveIntervention;
    private Integer spaceBehind;
    private Integer oppProgressedAndSpaceBehind;
    private Integer posessionRetained;
    private Integer underCP;
    private Integer succUnSuccPenetration;
    private Integer chanceCreated;
    private Integer createdDangPosession;
    private Integer counterPressAttempt;

}
