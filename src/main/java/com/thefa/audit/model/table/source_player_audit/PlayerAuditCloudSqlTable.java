package com.thefa.audit.model.table.source_player_audit;

import lombok.Data;

@Data
public class PlayerAuditCloudSqlTable {

    private String playerId;

    private String playerGrading;

    private String currentEnglandStatus;

    private String intelNotes;

    private String intelNotes2;

    private String intelNotes3;

    private String intelNotes4;

    private String maturationSummary;

    private String injuryIndex;

    private String lastModified;
}
