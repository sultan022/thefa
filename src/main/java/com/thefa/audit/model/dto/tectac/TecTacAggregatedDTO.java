package com.thefa.audit.model.dto.tectac;

import com.thefa.audit.model.shared.TecTacPossession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class TecTacAggregatedDTO {

    private String tectac;
    private String value;
    private TecTacPossession possession;
}
