package com.thefa.audit.model.kind;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "PlayerPerformance")
public class PlayerPerformanceKind {

    @Id
    private String id;
    private String date;
    private String lastModified;
    private Double avgRSR_KG;
    private Double avgRSR;
    private Double bodyWeightKg;
    private Double contactTime_KG;
    private Double contactTime;
    private Double counterMovementDepthCM;
    private Double eccentricDurationMS;
    private Double eccentricMeanPowerKG;
    private Double flightTime_KG;
    private Double flightTime;
    private Double mas;
    private Double maxSpeed;
    private Double peakForceBilateralNKG_hamstring;
    private Double peakForceBilateralNKG_prone;
    private Double peakForceBilateralN_hamstring;
    private Double peakForceBilateralN_prone;
    private Integer primaryPositionNumber;
    private Integer rankAvgRSR;
    private Integer rankCmj;
    private Integer rankMas;
    private Integer rankProneISO;
    private String englandSquadId;
    private String campGroup;
    private String campId;
    private String currentClubId;
    private String currentClubName;
    private String firstName;
    private String fullName;
    private String gender;
    private String playerPhotoURL;
    private String lastName;
    private String playerId;

}
