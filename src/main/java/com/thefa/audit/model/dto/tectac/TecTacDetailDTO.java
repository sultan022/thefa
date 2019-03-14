package com.thefa.audit.model.dto.tectac;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class TecTacDetailDTO {
    private String date;
    private String videoLink;
    private String downloadLink;
    private Double startTime;
    private Double endTime;
    private String homeTeam;
    private String homeScore;
    private String awayTeam;
    private String awayScore;
}
