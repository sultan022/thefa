package com.thefa.audit.model.kind;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "PlayerStatsMinutesSummary")
public class PlayerStatsMinutesSummaryKind {

    @Id
    private String id;
    private String playerId;
    private String foreignPlayerId;
    private String firstName;
    private String lastName;
    private Integer statGamesPlayed;
    private Integer statTimePlayed;
    private Integer statStarts;
    private Integer statSubstituteOff;
    private String teamGames;
    private String teamType;
    private String seasonId;
    private String seasonName;
    private String competitionId;
    private String competitionName;
    private Integer clubMinutesPlayed;
    private Integer clubMinutesRanking;
    private String source;
    private String lastModified;

}
