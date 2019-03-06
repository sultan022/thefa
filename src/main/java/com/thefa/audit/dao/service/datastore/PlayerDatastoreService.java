package com.thefa.audit.dao.service.datastore;

import com.thefa.audit.dao.repository.datastore.PlayerDSRepository;
import com.thefa.audit.model.dto.player.base.PlayerDTO;
import com.thefa.audit.model.dto.player.base.PlayerGradeDTO;
import com.thefa.audit.model.dto.player.base.PlayerPositionDTO;
import com.thefa.audit.model.dto.player.base.PlayerSquadDTO;
import com.thefa.audit.model.dto.rerference.ClubDTO;
import com.thefa.audit.model.dto.rerference.CountryDTO;
import com.thefa.audit.model.kind.PlayerKind;
import com.thefa.audit.model.kind.PlayerSquadKind;
import com.thefa.audit.model.kind.PositionKind;
import com.thefa.audit.model.shared.InjuryStatus;
import com.thefa.audit.model.shared.MaturationStatus;
import com.thefa.audit.model.shared.SquadStatusType;
import com.thefa.common.dto.shared.SquadType;
import one.util.streamex.StreamEx;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class PlayerDatastoreService {

    private final PlayerDSRepository playerDSRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public PlayerDatastoreService(PlayerDSRepository playerDSRepository,
                                  ModelMapper modelMapper) {

        this.playerDSRepository = playerDSRepository;
        this.modelMapper = modelMapper;
    }

    @Async
    @SuppressWarnings("Duplicates")
    public void createPlayerKindFromDTO(PlayerDTO playerDTO) {
        CompletableFuture.supplyAsync(() -> {
            PlayerKind kind = new PlayerKind();

            kind.setId(playerDTO.getPlayerId());
            updatePlayerKindFromDTO(kind, playerDTO);
            playerDSRepository.save(kind);

            return null;
        });
    }

    @Async
    @SuppressWarnings("Duplicates")
    public void updatePlayerKindFromDTO(PlayerDTO playerDTO) {
        CompletableFuture.supplyAsync(() -> {

            StreamEx.of(playerDSRepository.findAllByPlayerId(playerDTO.getPlayerId()))
                    .peek(kind -> updatePlayerKindFromDTO(kind, playerDTO))
                    .forEach(playerDSRepository::save);

            return null;
        });
    }

    @SuppressWarnings("Duplicates")
    private void updatePlayerKindFromDTO(PlayerKind kind, PlayerDTO playerDTO) {
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

        PlayerSquadDTO primarySquadDTO = StreamEx.of(playerDTO.getPlayerSquads())
                .sortedByInt(squad -> squad.getSquadType().ordinal())
                .findFirst()
                .orElse(null);

        kind.setIsEnglandPlayer(
                StreamEx.of(playerDTO.getEligibilities()).map(CountryDTO::getCountryName).has("England")
        );

        String primarySquad = Optional.ofNullable(primarySquadDTO).map(PlayerSquadDTO::getSquadType).map(SquadType::name).orElse(null);
        String primarySquadStatus = Optional.ofNullable(primarySquadDTO).map(PlayerSquadDTO::getStatusType).map(SquadStatusType::name).orElse(null);

        kind.setEnglandSquadId(primarySquad);
        kind.setCurrentEnglandStatus(primarySquadStatus);
        kind.setProposedNewEnglandStatus(primarySquadStatus);
        kind.setPlayerGrading(
                Optional.ofNullable(playerDTO.getPlayerGrade()).map(PlayerGradeDTO::getGrade).orElse(null)
        );
        kind.setInjuryIndex(Optional.ofNullable(playerDTO.getInjuryStatus()).map(InjuryStatus::name).orElse(null));
        kind.setExpectedReturnDate(Optional.ofNullable(playerDTO.getExpectedReturnDate()).map(LocalDate::toString).orElse(null));

        Integer primaryPosition = StreamEx.of(playerDTO.getPlayerPositions())
                .sortedByInt(PlayerPositionDTO::getPositionOrder)
                .findFirst()
                .map(PlayerPositionDTO::getPositionNumber)
                .orElse(null);

        kind.setPrimaryPositionNumber(primaryPosition);
        StreamEx.of(playerDTO.getPlayerSquads())
                .map(squadDTO -> new PlayerSquadKind(
                        Optional.ofNullable(squadDTO.getSquadType()).map(SquadType::name).orElse(null),
                        Optional.ofNullable(squadDTO.getStatusType()).map(SquadStatusType::name).orElse(null)))
                .toListAndThen(squads -> {
                    kind.setPlayerSquad(squads);
                    return null;
                });

        StreamEx.of(playerDTO.getPlayerPositions())
                .map(positionDTO -> new PositionKind(positionDTO.getPositionNumber(), positionDTO.getPositionOrder()))
                .toListAndThen(positions -> {
                    kind.setPosition(positions);
                    return null;
                });
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
    }

}
