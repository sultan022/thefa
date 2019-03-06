package com.thefa.audit.model.kind;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Entity(name = "Player")
@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerKind extends AbstractPlayerKind {

    @Id
    private String id;
    private String playerId;
    private String firstName;
    private String lastName;
    private String fullName;
    private Integer originalOptaPlayerId;
    private String foreignPlayerId;
    private String englandSquadId;
    private String gender;
    private String dateOfBirth;
    private Integer primaryPositionNumber;
    private String currentParentClubName;
    private String currentClubName;
    private String currentEnglandStatus;
    private String proposedNewEnglandStatus;
    private String playerGrading;
    private Integer totalCoachReports;
    private Integer currentSeasonMinutesPlayed;
    private Integer totalCaps;
    private Integer currentSeasonGamesPlayed;
    private Boolean isEnglandPlayer;
    private String maturationValue;
    private String maturationSummary;
    private String capabilityIndex;
    private String vulnerabilityIndex;
    private String injuryIndex;
    private String injuryStatus;
    private String expectedReturnDate;
    private Double scoreLast12MPercent;
    private Integer reportCountLast12M;
    private Integer intMins;
    private Integer intGames;
    private Integer curSeasonMins;
    private Integer curSeasonGames;
    private Integer prevSeasonMins;
    private Integer prevSeasonGames;
    private Integer curSeasonHasStarted;
    private Integer curSeasonYellowCards;
    private Integer curSeasonSecondYellowCards;
    private Integer curSeasonRedCards;
    private Integer curSeasonSubOn;
    private Integer curSeasonSubOff;
    private Integer prevSeasonHasStarted;
    private Integer prevSeasonYellowCards;
    private Integer prevSeasonSecondYellowCards;
    private Integer prevSeasonRedCards;
    private Integer prevSeasonSubOn;
    private Integer prevSeasonSubOff;
    private Integer intHasStarted;
    private Integer intYellowCards;
    private Integer intSecondYellowCards;
    private Integer intRedCards;
    private Integer intSubOn;
    private Integer intSubOff;
    private Integer curSeasonIntMins;
    private Integer curSeasonIntGames;
    private Integer curSeasonIntHasStarted;
    private Integer curSeasonIntYellowCards;
    private Integer curSeasonIntSecondYellowCards;
    private Integer curSeasonIntRedCards;
    private Integer curSeasonIntSubOn;
    private Integer curSeasonIntSubOff;
    private Integer curSeasonRank;
    private Integer prevSeasonRank;
    private Integer intRank;
    private Integer curSeasonIntRank;
    private String vulnerabilityDate;
    private Integer vulnerabilityStatus;
    private Integer vulnerabilityStatus4Weeks;
    private Integer vulnerabilityStatus8Weeks;
    private Integer vulnerabilityStatus12Weeks;
    private String maturationDate;
    private String maturationStatus;

}
