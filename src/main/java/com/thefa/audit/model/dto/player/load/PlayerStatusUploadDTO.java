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

    @JsonIgnore
    public boolean isEmpty() {
        return this.maturationStatus == null && this.maturationDate == null && this.vulnerabilityStatus == null && this.vulnerabilityDate == null;
    }

    @JsonIgnore
    public boolean isValidMaturation() {
        return (this.maturationStatus != null && this.maturationDate != null);

    }

    @JsonIgnore
    public boolean isValidVulnerability() {
        return (this.vulnerabilityStatus != null && this.vulnerabilityDate != null);

    }

    @JsonIgnore
    public boolean isValidData() {
        return isValidMaturation() || isValidVulnerability();
    }

}
