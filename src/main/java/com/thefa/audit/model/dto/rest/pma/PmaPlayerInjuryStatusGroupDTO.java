package com.thefa.audit.model.dto.rest.pma;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thefa.audit.model.shared.InjuryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PmaPlayerInjuryStatusGroupDTO {

    @JsonProperty("InjuryStatusGroup")
    private String injuryStatusGroup;

    @JsonProperty("EstimatedReturnDate")
    private PmaDate estimatedReturnDate;

    public InjuryStatus toInjuryStatus() {
        switch (injuryStatusGroup.toLowerCase()) {
            case "fit":
                return InjuryStatus.GREEN;
            case "fit to train":
                return InjuryStatus.AMBER;
            case "injured":
            case "illness":
                return InjuryStatus.RED;
        }

        return null;
    }
}
