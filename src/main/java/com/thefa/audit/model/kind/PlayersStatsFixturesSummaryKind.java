package com.thefa.audit.model.kind;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "PlayersStatsFixturesSummary")
public class PlayersStatsFixturesSummaryKind {


    @Id
    private String id;
    private String fixtureDate;
    private String fixtureDateTime;
    private String homeTeamName;
    private String homeClubName;
    private String awayTeamName;
    private String awayClubName;
    private String competitionName;
    private String competitionSortOrder;
    private String seasonName;
    private String gender;
    private String fullName;
    private Integer primaryPositionNumber;
    private Integer pos;
    private Integer positionNumber;
    private Boolean started;
    private String playerId;
    private String competitionId;
    private String seasonId;
    private String homeClubId;
    private String awayClubId;
    private String fixtureId;
    private String foreignPlayerId;
    private String foreignFixtureId;
    private String foreignHomeTeamId;
    private String foreignAwayTeamId;
    private String foreignCompetitionId;
    private String foreignSeasonId;
    private Integer goals;
    private Integer ownGoals;
    private Integer saves;
    private Integer redCards;
    private Integer yellowCards;
    private Boolean substituteOn;
    private Boolean substituted;
    private String substituteOnTime;
    private String substitutedOffTime;
    private Boolean isGoalKeeper;
    private Integer minutesPlayed;
    private Integer homeGoals;
    private Integer awayGoals;
    private String optaFixtureId;
    private Integer matchDuration;
    private String source;
    private String lastModified;
    private String teamType;
}
