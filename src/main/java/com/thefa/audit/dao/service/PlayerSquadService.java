package com.thefa.audit.dao.service;

import com.thefa.audit.dao.repository.player.PlayerSquadHistoryRepository;
import com.thefa.audit.dao.repository.player.PlayerSquadRepository;
import com.thefa.audit.model.dto.player.base.PlayerSquadDTO;
import com.thefa.audit.model.dto.player.edit.BulkEditPlayerMultipleSquadDTO;
import com.thefa.audit.model.dto.player.edit.BulkEditPlayerSingleSquadDTO;
import com.thefa.audit.model.dto.player.edit.EditPlayerSquadDTO;
import com.thefa.audit.model.dto.player.history.PlayerSquadHistoryDTO;
import com.thefa.audit.model.dto.player.specific.CompletePlayerSquadDTO;
import com.thefa.audit.model.entity.history.PlayerSquadHistory;
import com.thefa.audit.model.entity.player.Player;
import com.thefa.audit.model.entity.player.PlayerSquad;
import com.thefa.audit.model.entity.reference.Squad;
import com.thefa.audit.model.entity.reference.SquadStatus;
import com.thefa.audit.model.shared.Assignment;
import com.thefa.audit.model.shared.SquadStatusType;
import com.thefa.common.dto.shared.PageResponse;
import com.thefa.common.dto.shared.SquadType;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import lombok.val;
import one.util.streamex.StreamEx;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Order.desc;

@Service
@CommonsLog
@Transactional
public class PlayerSquadService {

    private final ReferenceService referenceService;

    private final PlayerSquadRepository playerSquadRepository;
    private final PlayerSquadHistoryRepository playerSquadHistoryRepository;
    private final ModelMapper modelMapper;

    public PlayerSquadService(ReferenceService referenceService,
                              PlayerSquadRepository playerSquadRepository,
                              PlayerSquadHistoryRepository playerSquadHistoryRepository,
                              ModelMapper modelMapper) {
        this.referenceService = referenceService;
        this.playerSquadRepository = playerSquadRepository;
        this.playerSquadHistoryRepository = playerSquadHistoryRepository;
        this.modelMapper = modelMapper;
    }


    public PageResponse<PlayerSquadHistoryDTO> findPlayerSquadHistory(int page, int size, String playerId) {
        val pageRequest = PageRequest.of(page, size, Sort.by(desc("createdAt")));
        final Page<PlayerSquadHistory> result = playerSquadHistoryRepository.findByPlayerId(playerId, pageRequest);

        return PageResponse
                .<PlayerSquadHistoryDTO>builder()
                .page(page)
                .size(size)
                .totalPages(result.getTotalPages())
                .totalSize(result.getTotalElements())
                .content(result.get().map(entity -> modelMapper.map(entity, PlayerSquadHistoryDTO.class)).collect(Collectors.toList()))
                .build();
    }

    public List<CompletePlayerSquadDTO> findAllByPlayerIdIn(Set<String> playerIds) {
        return playerSquadRepository.findAllByPlayerIdIn(playerIds)
                .map(entity -> modelMapper.map(entity, CompletePlayerSquadDTO.class))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("Duplicates")
    public void updatePlayerSquads(Player player, Set<PlayerSquadDTO> playerSquads) {

        Set<PlayerSquad> currentSquads = player.getPlayerSquads();

        Set<SquadType> updateSquadsReq = StreamEx.of(playerSquads)
                .map(PlayerSquadDTO::getSquadType)
                .toSet();

        Set<SquadType> existingSquads = StreamEx.of(currentSquads)
                .map(PlayerSquad::getSquadType)
                .toSet();

        List<PlayerSquadHistory> history = new ArrayList<>();

        // Delete any Squads which have been removed
        StreamEx.of(currentSquads)
                .filter(currentSquad -> !updateSquadsReq.contains(currentSquad.getSquadType()))
                .forEach(playerSquad -> history.add(new PlayerSquadHistory(player.getPlayerId(),
                        playerSquad.getSquadType(), playerSquad.getStatusType(), Assignment.REMOVED)));
        currentSquads.removeIf(currentSquad -> !updateSquadsReq.contains(currentSquad.getSquadType()));

        // Update any Modified Squad
        StreamEx.of(currentSquads)
                .forEach(currentSquad -> StreamEx.of(playerSquads).findAny(squadUpdateReq ->
                        squadUpdateReq.getSquadType() == currentSquad.getSquadType() &&
                                squadUpdateReq.getStatusType() != currentSquad.getStatusType())
                        .ifPresent(modified -> {
                            currentSquad.setStatusType(modified.getStatusType());
                            history.add(new PlayerSquadHistory(player.getPlayerId(),
                                    modified.getSquadType(), modified.getStatusType(), Assignment.UPDATED));
                        }));

        // Add any new Squads
        StreamEx.of(playerSquads)
                .filter(squadDTO -> !existingSquads.contains(squadDTO.getSquadType()))
                .forEach(newSquad -> {
                    currentSquads.add(new PlayerSquad(player.getPlayerId(),
                            referenceService.findSquad(newSquad.getSquadType()).orElse(null),
                            SquadStatus.fromStatusType(newSquad.getStatusType()),
                            null));
                    history.add(new PlayerSquadHistory(player.getPlayerId(),
                            newSquad.getSquadType(), newSquad.getStatusType(), Assignment.ADDED));
                });

        playerSquadHistoryRepository.saveAll(history);

    }

    public List<PlayerSquadHistory> bulkUpdatePlayersSquads(@NonNull List<Player> players,
                                                            @NonNull BulkEditPlayerSingleSquadDTO bulkEditPlayerSquadsDTO) {

        List<PlayerSquadHistory> history = new ArrayList<>();

        StreamEx.of(players)
                .forEach(player -> {

                    Set<PlayerSquad> playerSquads = player.getPlayerSquads();

                    Squad toSquad = referenceService.findSquad(bulkEditPlayerSquadsDTO.getToSquad()).orElse(null);

                    StreamEx.of(playerSquads)
                            .findAny(playerSquad -> playerSquad.getSquadType().equals(bulkEditPlayerSquadsDTO.getFromSquad()))
                            .ifPresent(playerSquad -> {

                                SquadStatusType fromStatusType = playerSquad.getStatusType();
                                SquadStatus toStatus = Optional.ofNullable(bulkEditPlayerSquadsDTO.getToStatus())
                                        .map(SquadStatus::fromStatusType)
                                        .orElse(playerSquad.getStatus());
                                SquadStatusType toStatusType = Optional.ofNullable(toStatus)
                                        .map(SquadStatus::getStatus)
                                        .map(SquadStatusType::valueOf)
                                        .orElse(null);

                                playerSquads.remove(playerSquad);
                                playerSquads.add(new PlayerSquad(player.getPlayerId(), toSquad, toStatus, null));

                                history.add(
                                        new PlayerSquadHistory(player.getPlayerId(), bulkEditPlayerSquadsDTO.getFromSquad(), fromStatusType, Assignment.REMOVED)
                                );

                                history.add(
                                        new PlayerSquadHistory(player.getPlayerId(), bulkEditPlayerSquadsDTO.getToSquad(),
                                                toStatusType, Assignment.ADDED)
                                );

                            });

                });

        return history;

    }

    @SuppressWarnings("Duplicates")
    public List<PlayerSquadHistory> bulkUpdatePlayerSquads(@NonNull List<Player> players,
                                                           @NonNull Set<BulkEditPlayerMultipleSquadDTO> squadDTOs) {

        List<PlayerSquadHistory> history = new ArrayList<>();

        StreamEx.of(players)
                .forEach(player -> StreamEx.of(squadDTOs).findAny(dto -> dto.getPlayerId().equals(player.getPlayerId()))
                        .ifPresent(dto -> {

                            Set<PlayerSquad> playerSquads = player.getPlayerSquads();

                            Set<SquadType> dbSquads = StreamEx.of(playerSquads).map(PlayerSquad::getSquadType).toSet();
                            Set<SquadType> uiSquads = StreamEx.of(dto.getSquads()).map(EditPlayerSquadDTO::getSquad).toSet();

                            // Updated Squads
                            Set<EditPlayerSquadDTO> updatedSquads = StreamEx.of(dto.getSquads())
                                    .filter(dtoSquads -> dbSquads.contains(dtoSquads.getSquad()))
                                    .filter(dtoSquads -> StreamEx.of(playerSquads)
                                            .findAny(dbSquad -> dbSquad.getSquadType() == dtoSquads.getSquad() && dbSquad.getStatusType() != dtoSquads.getStatus()).isPresent())
                                    .toSet();

                            StreamEx.of(updatedSquads)
                                    .forEach(updateSquad -> {
                                        StreamEx.of(playerSquads)
                                                .findAny(playerSquad -> playerSquad.getSquadType() == updateSquad.getSquad())
                                                .ifPresent(playerSquad -> playerSquad.setStatus(
                                                        Optional.ofNullable(updateSquad.getStatus())
                                                            .map(s -> new SquadStatus(s.name(), null))
                                                            .orElse(null)
                                                ));
                                        history.add(new PlayerSquadHistory(dto.getPlayerId(), updateSquad.getSquad(), updateSquad.getStatus(), Assignment.UPDATED));
                                    });


                            // New Squads
                            Set<EditPlayerSquadDTO> newSquads = StreamEx.of(dto.getSquads())
                                    .filter(dtoSquads -> !dbSquads.contains(dtoSquads.getSquad()))
                                    .toSet();

                            StreamEx.of(newSquads)
                                    .forEach(newSquad -> {
                                        playerSquads.add(new PlayerSquad(player.getPlayerId(),
                                                referenceService.findSquad(newSquad.getSquad()).orElse(null),
                                                SquadStatus.fromStatusType(newSquad.getStatus()),
                                                null));
                                        history.add(new PlayerSquadHistory(dto.getPlayerId(), newSquad.getSquad(), newSquad.getStatus(), Assignment.ADDED));
                                    });


                            // Deleted Squads
                            Set<SquadType> deletedSquads = StreamEx.of(dbSquads)
                                    .filter(dbSquad -> !uiSquads.contains(dbSquad))
                                    .toSet();

                            playerSquads.removeIf(playerSquad -> deletedSquads.contains(playerSquad.getSquadType()));
                            StreamEx.of(deletedSquads)
                                    .forEach(deletedSquad -> history.add(new PlayerSquadHistory(dto.getPlayerId(), deletedSquad, null, Assignment.REMOVED)));

                        }));

        return history;

    }
}
