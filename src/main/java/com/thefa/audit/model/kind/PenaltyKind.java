package com.thefa.audit.model.kind;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Penalty")
public class PenaltyKind {

    @Id
    private String id;
    private Boolean isSubstituteOn;
    private String lastModified;
    private Integer eventMinute;
    private Integer eventSecond;
    private String foreignGoalkeeperPlayerId;
    private String goalKeeperFullName;
    private String goalKeeperPlayerId;
    private String playerId;
    private String competitionId;
    private String eventTime;
    private String fixtureDate;
    private String fixtureRoundName;
    private String foreignEventId;
    private String foreignFixtureId;
    private String foreignPenaltyId;
    private String foreignTakerPlayerId;
    private String fullName;
    private String optaSource;
    private String penaltyResult;
    private String periodName;
    private String period;
    private String seasonId;
    private String teamSide;
    private String teamType;

}
