package com.thefa.audit.model.kind;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "PenaltyPlayer")
public class PenaltyPlayerKind {

    @Id
    private String id;
    private String fullName;
    private String firstName;
    private String lastName;
    private String gender;
    private String foreignPlayerId;
    private Integer isGoalKeeper;
    private Integer goalsTotal;
    private Integer savedTotal;
    private Integer offTargetTotal;
    private Integer penaltyScoredPercent;
    private Integer rightFootersTotal;
    private Integer rightFootersSavedPercent;
    private Integer rightFootersGoals;
    private Integer rightFootersDirectionLeft;
    private Integer rightFootersDirectionMiddle;
    private Integer rightFootersDirectionRight;
    private Integer leftFootersTotal;
    private Integer leftFootersSavedPercent;
    private Integer leftFootersGoals;
    private Integer leftFootersDirectionLeft;
    private Integer leftFootersDirectionMiddle;
    private Integer leftFootersDirectionRight;
    private Integer pressureTotal;
    private Integer pressureSavedPercent;
    private Integer pressureGoals;
    private Integer pressureDirectionLeft;
    private Integer pressureDirectionMiddle;
    private Integer pressureDirectionRight;
    private List<PenaltyDetailsKind> penalties;


}