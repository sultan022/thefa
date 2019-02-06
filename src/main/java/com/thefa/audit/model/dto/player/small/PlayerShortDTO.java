package com.thefa.audit.model.dto.player.small;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerShortDTO {

    @EqualsAndHashCode.Include
    private String playerId;

    private String firstName;

    private String middleName;

    private String lastName;

    private String knownName;

    private LocalDate dateOfBirth;

    private String profileImage;

}
