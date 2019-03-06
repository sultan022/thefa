package com.thefa.audit.dao.service;

import com.thefa.audit.dao.repository.player.PlayerPositionHistoryRepository;
import com.thefa.audit.model.dto.player.base.PlayerPositionDTO;
import com.thefa.audit.model.entity.history.PlayerPositionHistory;
import com.thefa.audit.model.entity.player.Player;
import com.thefa.audit.model.entity.player.PlayerPosition;
import com.thefa.audit.model.shared.Assignment;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class PlayerPositionService {

    private final PlayerPositionHistoryRepository playerPositionHistoryRepository;

    public PlayerPositionService(PlayerPositionHistoryRepository playerPositionHistoryRepository) {
        this.playerPositionHistoryRepository = playerPositionHistoryRepository;
    }


    @SuppressWarnings("Duplicates")
    public void updatePlayerPositions(Player player, Set<PlayerPositionDTO> playerPositions) {

        Set<PlayerPosition> currentPositions = player.getPlayerPositions();

        Set<Integer> updatePositionsReq = StreamEx.of(playerPositions)
                .map(PlayerPositionDTO::getPositionNumber)
                .toSet();

        Set<Integer> existingPositions = StreamEx.of(currentPositions)
                .map(PlayerPosition::getPositionNumber)
                .toSet();

        List<PlayerPositionHistory> history = new ArrayList<>();

        // Delete any Positions which have been removed
        StreamEx.of(currentPositions)
                .filter(currentPosition -> !updatePositionsReq.contains(currentPosition.getPositionNumber()))
                .forEach(playerPosition -> history.add(new PlayerPositionHistory(player.getPlayerId(),
                        playerPosition.getPositionNumber(), playerPosition.getPositionOrder(), Assignment.REMOVED)));
        currentPositions.removeIf(currentPosition -> !updatePositionsReq.contains(currentPosition.getPositionNumber()));

        // Update any Modified Positions
        StreamEx.of(currentPositions)
                .forEach(currentPosition -> StreamEx.of(playerPositions).findAny(posUpdateReq ->
                        posUpdateReq.getPositionNumber() == currentPosition.getPositionNumber() && posUpdateReq.getPositionOrder() != currentPosition.getPositionOrder())
                .ifPresent(modified -> {
                    currentPosition.setPositionOrder(modified.getPositionOrder());
                    history.add(new PlayerPositionHistory(player.getPlayerId(),
                            modified.getPositionNumber(), modified.getPositionOrder(), Assignment.UPDATED));
                }));


        // Add any new Positions
        StreamEx.of(playerPositions)
                .filter(positionDTO -> !existingPositions.contains(positionDTO.getPositionNumber()))
                .forEach(newPos -> {
                    currentPositions.add(new PlayerPosition(player.getPlayerId(), newPos.getPositionNumber(), newPos.getPositionOrder(), null));
                    history.add(new PlayerPositionHistory(player.getPlayerId(),
                            newPos.getPositionNumber(), newPos.getPositionOrder(), Assignment.ADDED));
                });

        playerPositionHistoryRepository.saveAll(history);

    }
}
