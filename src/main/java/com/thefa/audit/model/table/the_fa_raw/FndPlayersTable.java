package com.thefa.audit.model.table.the_fa_raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import com.thefa.audit.model.dto.player.base.PlayerDTO;
import com.thefa.audit.model.dto.player.base.PlayerPositionDTO;
import com.thefa.audit.model.dto.player.base.PlayerSocialDTO;
import com.thefa.audit.model.dto.rerference.CountryDTO;
import com.thefa.common.dto.shared.Gender;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import static com.thefa.audit.dao.service.PlayerInternalMappingCounterService.INTERNAL_ID_PREFIX;

@Data
public class FndPlayersTable {

    private String playerId;

    private String fullName;

    private String firstName;

    private String lastName;

    private String nickName;

    private String dateOfBirth;

    private String cityOfBirth;

    private String nationality;

    private String gender;

    private String otherEligibileNation;

    private String otherEligibileNation2;

    @JsonProperty("isEnglandPlayer")
    @Getter(AccessLevel.NONE) // Required for Object Mapper issue
    private boolean isEnglandPlayer;

    private String playerPhotoURL;

    private String thumbnailImage;

    private Integer primaryPositionNumber;

    private Integer secondPositionNumber;

    private Integer thirdPostitionNumber;

    private String englandSquadId;

    private String currentClubId;

    private String currentClubName;

    private String currentParentClubId;

    private String currentParentClubName;

    private String currentClubEndContractDate;

    private String twitterId;

    private String facebookId;

    private String wikipediaUrl;

    private String transfermarketUrl;

    private String modifiedTime;

    private String createdTime;

    private String archivedTime;

    private String ageCategory;

    private String currentTeamLevel;

    @JsonProperty("isEngland")
    @Getter(AccessLevel.NONE) // Required for Object Mapper issue
    private boolean isEngland;

    private String lastModified;

    public static FndPlayersTable fromPlayerDTO(@NonNull PlayerDTO playerDTO) {

        FndPlayersTable fndPlayers = new FndPlayersTable();

        fndPlayers.playerId = playerDTO.getPlayerId().replace(INTERNAL_ID_PREFIX, "");

        fndPlayers.fullName = StreamEx.of(playerDTO.getFirstName(), playerDTO.getMiddleName(), playerDTO.getLastName())
                                .filter(Objects::nonNull)
                                .joining(" ");

        fndPlayers.firstName = playerDTO.getFirstName();
        fndPlayers.lastName = playerDTO.getLastName();
        fndPlayers.nickName = playerDTO.getKnownName();

        fndPlayers.dateOfBirth = Optional.ofNullable(playerDTO.getDateOfBirth()).map(LocalDate::toString).orElse(null);

        fndPlayers.gender = playerDTO.getGender() == null ? null : (playerDTO.getGender() == Gender.M ? "male" : "female");

        fndPlayers.playerPhotoURL = playerDTO.getProfileImage();
        fndPlayers.thumbnailImage = playerDTO.getThumbnailImage();

        StreamEx.of(playerDTO.getEligibilities()).findFirst(t -> t.getCountryCode().equals("ENG")).ifPresent(eng -> {
            fndPlayers.setNationality("England");
            fndPlayers.isEngland = true;
            fndPlayers.isEnglandPlayer = true;
        });

        EntryStream.of(StreamEx.of(playerDTO.getEligibilities()).filter(t -> !t.getCountryCode().equals("ENG"))
                .sortedBy(CountryDTO::getCountryName).limit(2).toList()).forKeyValue((i, v) -> {
            switch (i) {
                case 0:
                    fndPlayers.otherEligibileNation = v.getCountryName();
                    break;
                case 1:
                    fndPlayers.otherEligibileNation2 = v.getCountryName();
                    break;
            }
        });

        EntryStream.of(
                StreamEx.of(playerDTO.getPlayerPositions())
                        .sortedByInt(PlayerPositionDTO::getPositionOrder)
                        .limit(3)
                        .map(PlayerPositionDTO::getPositionNumber).toList()
        ).forKeyValue((i, p) -> {
            switch (i) {
                case 0:
                    fndPlayers.primaryPositionNumber = p;
                    break;
                case 1:
                    fndPlayers.secondPositionNumber = p;
                    break;
                case 2:
                    fndPlayers.thirdPostitionNumber = p;
                    break;
            }
        });

        fndPlayers.englandSquadId = StreamEx.of(playerDTO.getPlayerSquads())
                                        .sortedByInt(squad -> squad.getSquadType().ordinal())
                                        .findFirst()
                                        .map(squad -> squad.getSquadType().name())
                                        .orElse(null);
        fndPlayers.ageCategory = fndPlayers.englandSquadId;

        Optional.ofNullable(playerDTO.getClub())
                .ifPresent(club -> {
                    fndPlayers.currentClubId = club.getId();
                    fndPlayers.currentClubName = club.getName();
                });

        EntryStream.of(StreamEx.of(playerDTO.getPlayerSocials()).groupingBy(PlayerSocialDTO::getSocialMedia))
                .forKeyValue((socialMediaType, playerSocialDTOS) -> {
                    String link = StreamEx.of(playerSocialDTOS)
                            .reverseSorted(Comparator.comparing(PlayerSocialDTO::getCreatedAt))
                            .findFirst()
                            .map(PlayerSocialDTO::getLink).orElse(null);
                    switch (socialMediaType) {
                        case TWITTER:
                            fndPlayers.twitterId = link;
                            break;
                        case FACEBOOK:
                            fndPlayers.facebookId = link;
                            break;
                        case WIKIPEDIA:
                            fndPlayers.wikipediaUrl = link;
                            break;
                        case TRANSFER_MARKET:
                            fndPlayers.transfermarketUrl = link;
                            break;
                    }
                });

        fndPlayers.lastModified = Timestamp.now().toString();

        return fndPlayers;
    }
}
