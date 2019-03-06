package com.thefa.audit.model.kind;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "PlayerStats")
public class PlayerStatsKind {

    @Id
    private String id;
    private String firstName;
    private String fullName;
    private String lastName;
    private String foreignPlayerId;
    private String playerId;
    private String seasonId;
    private String foreignSeasonId;
    private String teamType;
    private String lastModified;

    @Field(name = "GK__Catches")
    private Double gkCatches;

    @Field(name = "GK__Crosses_Not_Claimed")
    private Double gkCrossesNotClaimed;

    @Field(name = "GK__Goals_Conceded")
    private Double gkGoalsConceded;

    @Field(name = "GK__Goals_Conceded_Inside_Box")
    private Double gkGoalsConcededInsideBox;

    @Field(name = "GK__Goals_Conceded_Outside_Box")
    private Double gkGoalsConcededOutsideBox;

    @Field(name = "GK__Saves_Made")
    private Double gkSavesMade;

    @Field(name = "GK__Saves_Made_From_Inside_Box")
    private Double gkSavesMadeFromInsideBox;

    @Field(name = "GK__Saves_Made_From_Outside_Box")
    private Double gkSavesMadeFromOutsideBox;

    @Field(name = "GK__Smother")
    private Double gkSmother;

    @Field(name = "GK__Successful_Distribution")
    private Double gkSuccessfulDistribution;

    @Field(name = "GK__Unsuccessful_Distribution")
    private Double gkUnsuccessfulDistribution;

    @Field(name = "Games_Played")
    private Integer gamesPlayed;

    @Field(name = "PASS__Goal_Assists")
    private Double passGoalAssists;

    @Field(name = "PASS__Successful_Crosses_Open_Play")
    private Double passSuccessfulCrossesOpenPlay;

    @Field(name = "PASS__Successful_Open_Play_Passes")
    private Double passSuccessfulOpenPlayPasses;

    @Field(name = "PASS__Successful_Passes")
    private Double passSuccessfulPasses;

    @Field(name = "PASS__Through_Balls")
    private Double passThroughBalls;

    @Field(name = "PASS__Touches")
    private Double passTouches;

    @Field(name = "PASS__Unsuccessful_Passes")
    private Double passUnsuccessfulPasses;

    @Field(name = "SHOT__Goals")
    private Double shotGoals;

    @Field(name = "SHOT__Headed_Goals")
    private Double shotHeadedGoals;

    @Field(name = "SHOT__Offsides")
    private Double shotOffsides;

    @Field(name = "TACKLE__Aerial_Duels_Lost")
    private Double tackleAerialDuelsLost;

    @Field(name = "TACKLE__Aerial_Duels_Won")
    private Double tackleAerialDuelsWon;

    @Field(name = "TACKLE__Blocked_Shots")
    private Double tackleBlockedShots;

    @Field(name = "TACKLE__Foul_Attempted_Tackle")
    private Double tackleFoulAttemptedTackle;

    @Field(name = "TACKLE__Ground_Duels_Lost")
    private Double tackleGroundDuelsLost;

    @Field(name = "TACKLE__Ground_Duels_Won")
    private Double tackleGroundDuelsWon;

    @Field(name = "TACKLE__Interceptions")
    private Double tackleInterceptions;

    @Field(name = "TACKLE__Last_Man_Tackle")
    private Double tackleLastManTackle;

    @Field(name = "TACKLE__Tackles_Lost")
    private Double tackleTacklesLost;

    @Field(name = "TACKLE__Tackles_Won")
    private Double tackleTacklesWon;

    @Field(name = "Time_Played")
    private Integer timePlayed;

}
