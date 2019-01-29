package com.thefa.audit.model.dto.rerference;

import com.thefa.audit.model.shared.TeamType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ClubDTO {

    @EqualsAndHashCode.Include
    @NotEmpty
    private String id;

    private String name;

    private String abbr;

    private String nickname;

    private String city;

    private String country;

    private String stadium;

    private String website;

    private Integer yearFounded;

    @Enumerated(EnumType.STRING)
    private TeamType teamType;

    private boolean isActive;

}
