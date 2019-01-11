package com.thefa.audit.model.dto.player.create;

import com.thefa.audit.model.dto.player.base.PlayerForeignMappingDTO;
import com.thefa.audit.model.dto.player.base.PlayerPositionDTO;
import com.thefa.audit.model.dto.player.base.PlayerSquadDTO;
import com.thefa.audit.model.dto.rerference.ClubDTO;
import com.thefa.audit.model.shared.Gender;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CreatePlayerDTO {

    @EqualsAndHashCode.Include
    @NotNull
    private Long fanId;

    @NotEmpty @Length(min = 3, max = 255)
    private String firstName;

    @Length(max = 255)
    private String middleName;

    @NotEmpty @Length(min = 3, max = 255)
    private String lastName;

    @Length(max = 255)
    private String knownName;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private Gender gender;

    @Length(max = 2)
    private String playerGrade;

    @Valid
    private ClubDTO club;

    @Valid
    private Set<PlayerForeignMappingDTO> foreignMappings = new HashSet<>();

    private Set<String> eligibilities = new HashSet<>();

    @Valid
    private Set<PlayerSquadDTO> playerSquads = new HashSet<>();

    @Valid
    private Set<CreatePlayerIntelDTO> playerIntels = new HashSet<>();

    @Valid
    private Set<PlayerPositionDTO> playerPositions = new HashSet<>();

    @Valid
    private Set<CreatePlayerSocialDTO> playerSocials = new HashSet<>();

}
