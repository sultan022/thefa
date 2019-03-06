package com.thefa.audit.model.kind;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "PlayerStatsFixturesPlaylist")
public class PlayerStatsFixturesPlaylistKind {

    @Id
    private String id;
    private Integer optaId;
    private Integer period;
    private String title;
    private Integer inTime;
    private Integer outTime;
    private Integer eventTypeId;
    private Integer eventPlayerId;
}