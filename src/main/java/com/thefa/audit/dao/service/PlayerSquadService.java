package com.thefa.audit.dao.service;

import com.thefa.audit.dao.repository.player.PlayerSquadHistoryRepository;
import com.thefa.audit.model.dto.player.edit.BulkEditPlayerMultipleSquadDTO;
import com.thefa.audit.model.dto.player.edit.BulkEditPlayerSingleSquadDTO;
import com.thefa.audit.model.dto.player.edit.EditPlayerSquadDTO;
import com.thefa.audit.model.dto.player.history.PlayerSquadHistoryDTO;
import com.thefa.audit.model.entity.history.PlayerSquadHistory;
import com.thefa.audit.model.entity.player.Player;
import com.thefa.audit.model.entity.player.PlayerSquad;
import com.thefa.audit.model.shared.Assignment;
import com.thefa.audit.model.shared.SquadType;
import com.thefa.common.dto.shared.PageResponse;
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
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Order.desc;

@Service
@CommonsLog
@Transactional
public class PlayerSquadService {

    private final PlayerSquadHistoryRepository playerSquadHistoryRepository;
    private final ModelMapper modelMapper;

    public PlayerSquadService(PlayerSquadHistoryRepository playerSquadHistoryRepository,
                              ModelMapper modelMapper) {
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

    public List<PlayerSquadHistory> bulkUpdatePlayersSquads(@NonNull List<Player> players,
                                                            @NonNull BulkEditPlayerSingleSquadDTO bulkEditPlayerSquadsDTO) {

        List<PlayerSquadHistory> history = new ArrayList<>();

        StreamEx.of(players)
                .forEach(player -> {

                    Set<PlayerSquad> playerSquads = player.getPlayerSquads();

                    playerSquads.removeIf(playerSquad -> playerSquad.getSquad().equals(bulkEditPlayerSquadsDTO.getFromSquad()));
                    playerSquads.add(new PlayerSquad(player.getPlayerId(), bulkEditPlayerSquadsDTO.getToSquad(),
                            bulkEditPlayerSquadsDTO.getToStatus(), null));

                    history.add(
                            new PlayerSquadHistory(player.getPlayerId(), bulkEditPlayerSquadsDTO.getFromSquad(), null, Assignment.REMOVED)
                    );

                    history.add(
                            new PlayerSquadHistory(player.getPlayerId(), bulkEditPlayerSquadsDTO.getToSquad(),
                                    bulkEditPlayerSquadsDTO.getToStatus(), Assignment.ADDED)
                    );

                });

        return history;

    }

    public List<PlayerSquadHistory> bulkUpdatePlayerSquads(@NonNull List<Player> players,
                                                           @NonNull Set<BulkEditPlayerMultipleSquadDTO> squadDTOs) {

        List<PlayerSquadHistory> history = new ArrayList<>();

        StreamEx.of(players)
                .forEach(player -> StreamEx.of(squadDTOs).findAny(dto -> dto.getPlayerId().equals(player.getPlayerId()))
                        .ifPresent(dto -> {

                            Set<PlayerSquad> playerSquads = player.getPlayerSquads();

                            Set<SquadType> dbSquads = StreamEx.of(playerSquads).map(PlayerSquad::getSquad).toSet();
                            Set<SquadType> uiSquads = StreamEx.of(dto.getSquads()).map(EditPlayerSquadDTO::getSquad).toSet();

                            // Updated Squads
                            Set<EditPlayerSquadDTO> updatedSquads = StreamEx.of(dto.getSquads())
                                    .filter(dtoSquads -> dbSquads.contains(dtoSquads.getSquad()))
                                    .filter(dtoSquads -> StreamEx.of(playerSquads)
                                            .findAny(dbSquad -> dbSquad.getSquad() == dtoSquads.getSquad() && dbSquad.getStatus() != dtoSquads.getStatus()).isPresent())
                                    .toSet();

                            StreamEx.of(updatedSquads)
                                    .forEach(updateSquad -> {
                                        StreamEx.of(playerSquads)
                                                .findAny(playerSquad -> playerSquad.getSquad() == updateSquad.getSquad())
                                                .ifPresent(playerSquad -> playerSquad.setStatus(updateSquad.getStatus()));
                                        history.add(new PlayerSquadHistory(dto.getPlayerId(), updateSquad.getSquad(), updateSquad.getStatus(), Assignment.UPDATED));
                                    });


                            // New Squads
                            Set<EditPlayerSquadDTO> newSquads = StreamEx.of(dto.getSquads())
                                    .filter(dtoSquads -> !dbSquads.contains(dtoSquads.getSquad()))
                                    .toSet();

                            StreamEx.of(newSquads)
                                    .forEach(newSquad -> {
                                        playerSquads.add(new PlayerSquad(player.getPlayerId(), newSquad.getSquad(), newSquad.getStatus(), null));
                                        history.add(new PlayerSquadHistory(dto.getPlayerId(), newSquad.getSquad(), newSquad.getStatus(), Assignment.ADDED));
                                    });


                            // Deleted Squads
                            Set<SquadType> deletedSquads = StreamEx.of(dbSquads)
                                    .filter(dbSquad -> !uiSquads.contains(dbSquad))
                                    .toSet();

                            playerSquads.removeIf(playerSquad -> deletedSquads.contains(playerSquad.getSquad()));
                            StreamEx.of(deletedSquads)
                                    .forEach(deletedSquad -> history.add(new PlayerSquadHistory(dto.getPlayerId(), deletedSquad, null, Assignment.REMOVED)));

                        }));

        return history;

    }
}
