package com.thefa.audit.model.kind;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "PlayerFutureFixtures")
public class PlayerFutureFixturesKind {

    @Id
    private String id;
    private String lastModified;
    private String awayClubId;
    private String awayTeamName;
    private String competitionId;
    private String competitionName;
    private String fixtureDateTime;
    private String fixtureDate;
    private String fixtureId;
    private String homeClubId;
    private String homeTeamName;
    private String playerId;
    private String seasonId;
    private String seasonName;
    private String source;

}
