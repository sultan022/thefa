package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.PlayerSummaryDSRepository;
import com.thefa.audit.model.dto.player.base.*;
import com.thefa.audit.model.dto.rerference.ClubDTO;
import com.thefa.audit.model.dto.rerference.CountryDTO;
import com.thefa.audit.model.kind.PlayerSummaryKind;
import com.thefa.audit.model.shared.InjuryStatus;
import com.thefa.audit.model.shared.MaturationStatus;
import com.thefa.audit.model.shared.SocialMediaType;
import com.thefa.audit.model.shared.SquadStatusType;
import com.thefa.common.dto.shared.SquadType;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class PlayerSummaryDatastoreService {

    private final PlayerSummaryDSRepository playerSummaryDSRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PlayerSummaryDatastoreService(PlayerSummaryDSRepository playerSummaryDSRepository,
                                         ModelMapper modelMapper) {
        this.playerSummaryDSRepository = playerSummaryDSRepository;
        this.modelMapper = modelMapper;
    }

    @Async
    @SuppressWarnings("Duplicates")
    public void createPlayerSummaryFromDTO(PlayerDTO playerDTO) {
        CompletableFuture.supplyAsync(() -> {
            PlayerSummaryKind kind = new PlayerSummaryKind();

            kind.setId(playerDTO.getPlayerId());
            updatePlayerSummaryFromDTO(kind, playerDTO);
            playerSummaryDSRepository.save(kind);

            return null;
        });
    }

    @Async
    @SuppressWarnings("Duplicates")
    public void updatePlayerSummaryFromDTO(PlayerDTO playerDTO) {
        CompletableFuture.supplyAsync(() -> {

            StreamEx.of(playerSummaryDSRepository.findAllByPlayerId(playerDTO.getPlayerId()))
                    .peek(kind -> updatePlayerSummaryFromDTO(kind, playerDTO))
                    .forEach(playerSummaryDSRepository::save);

            return null;
        });
    }

    @SuppressWarnings("Duplicates")
    private void updatePlayerSummaryFromDTO(PlayerSummaryKind kind, PlayerDTO playerDTO) {
        kind.setNickname(playerDTO.getKnownName());
        kind.setFirstName(playerDTO.getFirstName());
        kind.setLastName(playerDTO.getLastName());
        kind.setFullName(StreamEx.of(
                playerDTO.getFirstName(),
                playerDTO.getMiddleName(),
                playerDTO.getLastName())
                .filter(Objects::nonNull)
                .joining(" "));
        kind.setGender(playerDTO.getGender().name());
        kind.setDateOfBirth(playerDTO.getDateOfBirth().toString());
        kind.setCurrentClubName(
                Optional.ofNullable(playerDTO.getClub()).map(ClubDTO::getName).orElse(null)
        );
        kind.setCurrentClubId(
                Optional.ofNullable(playerDTO.getClub()).map(ClubDTO::getId).orElse(null)
        );

        kind.setPlayerPhotoURL(playerDTO.getProfileImage());

        PlayerSquadDTO primarySquadDTO = StreamEx.of(playerDTO.getPlayerSquads())
                .sortedByInt(squad -> squad.getSquadType().ordinal())
                .findFirst()
                .orElse(null);

        String primarySquad = Optional.ofNullable(primarySquadDTO).map(PlayerSquadDTO::getSquadType).map(SquadType::name).orElse(null);
        String primarySquadStatus = Optional.ofNullable(primarySquadDTO).map(PlayerSquadDTO::getStatusType).map(SquadStatusType::name).orElse(null);

        kind.setAgeCategory(primarySquad);
        kind.setEnglandSquadId(primarySquad);
        kind.setCurrentEnglandStatus(primarySquadStatus);
        kind.setProposedNewEnglandStatus(primarySquadStatus);
        kind.setPlayerGrading(
                Optional.ofNullable(playerDTO.getPlayerGrade()).map(PlayerGradeDTO::getGrade).orElse(null)
        );

        List<String> eligibilities = StreamEx.of(playerDTO.getEligibilities())
                .map(CountryDTO::getCountryName)
                .toList();
        if (eligibilities.contains("England")) {
            kind.setNationality("England");
            kind.setIsEngland(true);
            kind.setIsEnglandPlayer(true);
            List<String> remainingElig = StreamEx.of(eligibilities).without("England").sorted().toList();
            kind.setOtherEligibileNation(remainingElig.size() >= 1 ? remainingElig.get(0) : null);
            kind.setOtherEligibileNation2(remainingElig.size() >= 2 ? remainingElig.get(1) : null);
        } else {
            kind.setIsEngland(false);
            kind.setIsEnglandPlayer(false);
            List<String> allElig = StreamEx.of(eligibilities).sorted().toList();
            kind.setNationality(allElig.size() >= 1 ? allElig.get(1) : null);
            kind.setOtherEligibileNation(allElig.size() >= 2 ? allElig.get(1) : null);
            kind.setOtherEligibileNation2(allElig.size() >= 3 ? allElig.get(2) : null);
        }

        kind.setInjuryIndex(Optional.ofNullable(playerDTO.getInjuryStatus()).map(InjuryStatus::name).orElse(null));
        kind.setExpectedReturnDate(Optional.ofNullable(playerDTO.getExpectedReturnDate()).map(LocalDate::toString).orElse(null));

        List<Integer> positions = StreamEx.of(playerDTO.getPlayerPositions())
                .sortedByInt(PlayerPositionDTO::getPositionOrder)
                .map(PlayerPositionDTO::getPositionNumber)
                .toList();

        kind.setPrimaryPositionNumber(positions.size() >= 1 ? positions.get(0) : null);
        kind.setSecondPositionNumber(positions.size() >= 2 ? positions.get(1) : null);
        kind.setThirdPostitionNumber(positions.size() >= 3 ? positions.get(2) : null);

        kind.setVulnerabilityDate(
                Optional.ofNullable(playerDTO.getVulnerabilityDate()).map(LocalDate::toString).orElse(null)
        );
        kind.setVulnerabilityStatus(playerDTO.getVulnerabilityStatus());
        kind.setVulnerabilityStatus4Weeks(playerDTO.getVulnerabilityStatus4Weeks());
        kind.setVulnerabilityStatus8Weeks(playerDTO.getVulnerabilityStatus8Weeks());
        kind.setVulnerabilityStatus12Weeks(playerDTO.getVulnerabilityStatus12Weeks());
        kind.setMaturationDate(
                Optional.ofNullable(playerDTO.getMaturationDate()).map(LocalDate::toString).orElse(null)
        );
        kind.setMaturationStatus(
                Optional.ofNullable(playerDTO.getMaturationStatus()).map(MaturationStatus::name).orElse(null)
        );
        kind.setMaturationValue(
                Optional.ofNullable(playerDTO.getMaturationStatus()).map(MaturationStatus::colour).orElse(null)
        );

        Map<SocialMediaType, String> socials = EntryStream.of(StreamEx.of(playerDTO.getPlayerSocials()).groupingBy(PlayerSocialDTO::getSocialMedia))
                .mapValues(s -> StreamEx.of(s).reverseSorted(Comparator.comparing(PlayerSocialDTO::getCreatedAt))
                        .findFirst()
                        .map(PlayerSocialDTO::getLink).orElse(null))
                .toMap();

        kind.setFacebookId(socials.getOrDefault(SocialMediaType.FACEBOOK, null));
        kind.setTwitterId(socials.getOrDefault(SocialMediaType.TWITTER, null));
        kind.setTransfermarketUrl(socials.getOrDefault(SocialMediaType.TRANSFER_MARKET, null));
        kind.setWikipediaUrl(socials.getOrDefault(SocialMediaType.WIKIPEDIA, null));
    }
}
