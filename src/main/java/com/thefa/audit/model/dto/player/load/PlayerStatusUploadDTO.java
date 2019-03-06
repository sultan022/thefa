package com.thefa.audit.model.dto.player.load;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thefa.audit.model.shared.MaturationStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerStatusUploadDTO {

    @EqualsAndHashCode.Include
    @NotEmpty
    private String playerId;

    private MaturationStatus maturationStatus;

    private LocalDate maturationDate;

    @Range(min = 1, max = 7)
    private Integer vulnerabilityStatus;

    private LocalDate vulnerabilityDate;

    @Range(min = 1, max = 7)
    private Integer vulnerabilityStatus4Weeks;

    @Range(min = 1, max = 7)
    private Integer vulnerabilityStatus8Weeks;
    
    @Range(min = 1, max = 7)
    private Integer vulnerabilityStatus12Weeks;

    @JsonIgnore
    public boolean isEmpty() {
        return this.maturationStatus == null && this.maturationDate == null && this.vulnerabilityStatus == null && this.vulnerabilityDate == null
                && this.vulnerabilityStatus4Weeks == null && this.vulnerabilityStatus8Weeks == null && this.vulnerabilityStatus12Weeks == null;
    }


}
