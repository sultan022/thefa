package com.thefa.audit.model.dto.player.base;

import com.thefa.audit.model.dto.rerference.ClubDTO;
import com.thefa.audit.model.dto.rerference.CountryDTO;
import com.thefa.audit.model.shared.InjuryStatus;
import com.thefa.audit.model.shared.MaturationStatus;
import com.thefa.common.dto.shared.Gender;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerDTO {

    @EqualsAndHashCode.Include
    private String playerId;

    private String firstName;

    private String middleName;

    private String lastName;

    private String knownName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private PlayerGradeDTO playerGrade;

    private ClubDTO club;

    private String profileImage;

    private String thumbnailImage;

    private Set<PlayerForeignMappingDTO> foreignMappings = new HashSet<>();

    private Set<CountryDTO> eligibilities = new HashSet<>();

    private Set<PlayerSquadDTO> playerSquads = new HashSet<>();

    private Set<PlayerIntelDTO> playerIntels = new HashSet<>();

    private Set<PlayerPositionDTO> playerPositions = new HashSet<>();

    private Set<PlayerSocialDTO> playerSocials = new HashSet<>();

    private MaturationStatus maturationStatus;

    private LocalDate maturationDate;

    private Integer vulnerabilityStatus;

    private Integer vulnerabilityStatus4Weeks;

    private Integer vulnerabilityStatus8Weeks;

    private Integer vulnerabilityStatus12Weeks;

    private LocalDate vulnerabilityDate;

    private InjuryStatus injuryStatus;

    private LocalDate expectedReturnDate;

    private String createdBy;

    private ZonedDateTime createdAt;

    private String updatedBy;

    private ZonedDateTime updatedAt;

    private int version;

}
