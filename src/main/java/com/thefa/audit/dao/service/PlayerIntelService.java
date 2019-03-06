package com.thefa.audit.dao.service;

import com.thefa.audit.dao.repository.player.PlayerIntelRepository;
import com.thefa.audit.model.dto.player.base.PlayerIntelDTO;
import com.thefa.audit.model.dto.player.edit.EditPlayerIntelDTO;
import com.thefa.audit.model.entity.player.Player;
import com.thefa.audit.model.entity.player.PlayerIntel;
import com.thefa.audit.model.shared.IntelType;
import com.thefa.common.dto.shared.PageResponse;
import lombok.extern.apachecommons.CommonsLog;
import lombok.val;
import one.util.streamex.StreamEx;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Order.desc;

@Service
@CommonsLog
@Transactional
public class PlayerIntelService {

    private final PlayerIntelRepository playerIntelRepository;
    private final ModelMapper modelMapper;

    public PlayerIntelService(PlayerIntelRepository playerIntelRepository,
                              ModelMapper modelMapper) {
        this.playerIntelRepository = playerIntelRepository;
        this.modelMapper = modelMapper;
    }

    public boolean doAllIntelsExistAndBelongToPlayerId(List<Long> intelIds, String playerId) {
        return intelIds.size() == 0 || playerIntelRepository.countByPlayerIdAndIdInAndArchivedIsFalse(playerId, intelIds) == intelIds.size();
    }

    public PageResponse<PlayerIntelDTO> findPlayerIntels(int page, int size, String playerId, IntelType intelType) {
        val pageRequest = PageRequest.of(page, size, Sort.by(desc("createdAt")));
        final Page<PlayerIntel> result = Optional.ofNullable(intelType)
                .map(it -> playerIntelRepository.findByPlayerIdAndIntelType(playerId, it, pageRequest))
                .orElse(playerIntelRepository.findByPlayerId(playerId, pageRequest));

        return PageResponse
                .<PlayerIntelDTO>builder()
                .page(page)
                .size(size)
                .totalPages(result.getTotalPages())
                .totalSize(result.getTotalElements())
                .content(result.get().map(entity -> modelMapper.map(entity, PlayerIntelDTO.class)).collect(Collectors.toList()))
                .build();

    }

    public void updatePlayerIntels(Player entity, List<EditPlayerIntelDTO> intels) {

        List<Long> intelIdsFromClient = StreamEx.of(intels)
                .filter(intel -> intel.getId() != null)
                .map(EditPlayerIntelDTO::getId)
                .toList();

        List<PlayerIntel> allIntels = playerIntelRepository.findByPlayerId(entity.getPlayerId());


        // Archive deleted Intels
        StreamEx.of(allIntels)
                .filter(intel -> !intel.isArchived())
                .filter(intel -> !intelIdsFromClient.contains(intel.getId()))
                .peek(intel -> intel.setArchived(true))
                .toListAndThen(playerIntelRepository::saveAll);

        List<PlayerIntel> playerIntels = entity.getPlayerIntels();

        // Add new Intels
        StreamEx.of(intels)
                .filter(intel -> intel.getId() == null)
                .map(intel -> modelMapper.map(intel, PlayerIntel.class))
                .peek(intel -> intel.setPlayerId(entity.getPlayerId()))
                .forEach(playerIntels::add);

        // Update Intels
        StreamEx.of(intels)
                .filter(updatedIntel -> updatedIntel.getId() != null)
                .forEach(updatedIntel ->
                        StreamEx.of(playerIntels)
                                .findAny(intelEntity -> intelEntity.getId().equals(updatedIntel.getId()))
                                .ifPresent(intelEntity -> modelMapper.map(updatedIntel, intelEntity))
                );
    }
}
