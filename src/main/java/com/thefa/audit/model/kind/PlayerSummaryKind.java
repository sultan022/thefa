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
@Entity(name = "PlayerSummary")
public class PlayerSummaryKind {

    @Id
    private String id;
    private String ageCategory;
    private String archivedTime;
    private String capabilityIndex;
    private String cityOfBirth;
    private Integer coachM;
    private Integer coachNR;
    private Integer coachR;
    private String commentsOnProposal;
    private String createdTime;
    private String currentClubEndContractDate;
    private String currentClubId;
    private String currentClubName;
    private String currentEnglandStatus;
    private String currentParentClubId;
    private String currentParentClubName;
    private String currentTeamLevel;
    private String dateOfBirth;
    private String englandSquadId;
    private String facebookId;
    private String firstName;
    private String fullName;
    private String gender;
    private String injuryIndex;

    @Field(name = "InjuryStatus")
    private String injuryStatus;

    private String intelNotes;
    private String intelNotes2;
    private String intelNotes3;
    private String intelNotes4;
    private Boolean isEngland;
    private Boolean isEnglandPlayer;
    private String lastName;
    private String maturationSummary;
    private String maturationValue;
    private String modifiedTime;
    private String nationality;
    private String nickname;
    private String otherEligibileNation;
    private String otherEligibileNation2;
    private String playerGrading;
    private String playerId;
    private String playerPhotoURL;
    private Integer primaryPositionNumber;
    private String proposedNewEnglandStatus;
    private String reportingScore;
    private String reportSummary;
    private String seasonComments;
    private String seasonCommentsPrevSeas;
    private Integer secondPositionNumber;
    private Integer thirdPostitionNumber;
    private Integer totalCoachReports;
    private Integer totalReports;
    private Integer totalTRreports;
    private String transfermarketUrl;
    private Integer trM;
    private Integer trR;
    private String twitterId;
    private String vulnerabilityIndex;
    private String wikipediaUrl;
    private String vulnerabilityDate;
    private Integer vulnerabilityStatus;
    private Integer vulnerabilityStatus4Weeks;
    private Integer vulnerabilityStatus8Weeks;
    private Integer vulnerabilityStatus12Weeks;
    private String expectedReturnDate;
    private String maturationDate;
    private String maturationStatus;

}
