package com.thefa.audit.model.kind;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "SportsCodeVideos")
public class PlayerSportsCodeVideoKind {

    @Id
    private String id;
    private String fileId;
    private String filePath;
    private Double startTime;
    private Double endTime;

}