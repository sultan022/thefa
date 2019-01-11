package com.thefa.audit.dao.service;

import com.thefa.audit.dao.repository.player.*;
import com.thefa.audit.model.dto.player.base.PlayerDTO;
import com.thefa.audit.model.dto.player.base.PlayerIntelDTO;
import com.thefa.audit.model.dto.player.create.CreatePlayerDTO;
import com.thefa.audit.model.dto.player.edit.EditPlayerDTO;
import com.thefa.audit.model.dto.player.edit.EditPlayerIntelDTO;
import com.thefa.audit.model.dto.player.load.PlayerStatusUploadDTO;
import com.thefa.audit.model.dto.player.history.PlayerSquadHistoryDTO;
import com.thefa.audit.model.dto.player.load.PlayerGradeUploadDTO;
import com.thefa.audit.model.dto.player.small.PlayerShortDTO;
import com.thefa.audit.model.entity.history.PlayerGradeHistory;
import com.thefa.audit.model.entity.history.PlayerPositionHistory;
import com.thefa.audit.model.entity.history.PlayerSquadHistory;
import com.thefa.audit.model.entity.player.Player;
import com.thefa.audit.model.entity.player.PlayerForeignMapping;
import com.thefa.audit.model.entity.player.PlayerIntel;
import com.thefa.audit.model.shared.Assignment;
import com.thefa.audit.model.shared.DataSourceType;
import com.thefa.audit.model.shared.IntelType;
import com.thefa.common.dto.shared.PageResponse;

import static com.thefa.common.util.DateUtil.toDate;


import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import lombok.val;
import one.util.streamex.StreamEx;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;

@Service
@CommonsLog
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final ModelMapper modelMapper;
    private final PlayerInternalMappingCounterService playerCounterService;

    private final PlayerGradeHistoryRepository playerGradeHistoryRepository;
    private final PlayerPositionHistoryRepository playerPositionHistoryRepository;
    private final PlayerSquadHistoryRepository playerSquadHistoryRepository;
    private final PlayerIntelRepository playerIntelRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository,
                         PlayerGradeHistoryRepository playerGradeHistoryRepository,
                         PlayerPositionHistoryRepository playerPositionHistoryRepository,
                         PlayerSquadHistoryRepository playerSquadHistoryRepository,
                         PlayerIntelRepository playerIntelRepository,
                         PlayerInternalMappingCounterService playerCounterService,
                         ModelMapper modelMapper) {
        this.playerRepository = playerRepository;
        this.playerGradeHistoryRepository = playerGradeHistoryRepository;
        this.playerPositionHistoryRepository = playerPositionHistoryRepository;
        this.playerSquadHistoryRepository = playerSquadHistoryRepository;
        this.playerCounterService = playerCounterService;
        this.playerIntelRepository = playerIntelRepository;
        this.modelMapper = modelMapper;
    }

    public PageResponse<PlayerDTO> findPlayers(int page, int size, String search) {
        val pageRequest = PageRequest.of(page, size, Sort.by(asc("firstName")));

        Page<Player> result = playerRepository.findAll(pageRequest);

        return PageResponse
                .<PlayerDTO>builder()
                .page(page)
                .size(size)
                .totalPages(result.getTotalPages())
                .totalSize(result.getTotalElements())
                .content(result.get().map(entity -> modelMapper.map(entity, PlayerDTO.class)).collect(Collectors.toList()))
                .build();

    }

    public PageResponse<PlayerIntelDTO> findPlayerIntels(int page, int size, long fanId, IntelType intelType) {
        val pageRequest = PageRequest.of(page, size, Sort.by(desc("createdAt")));
        final Page<PlayerIntel> result = Optional.ofNullable(intelType)
                .map(it -> playerIntelRepository.findByFanIdAndIntelTypeAndArchivedIsFalse(fanId, it, pageRequest))
                .orElse(playerIntelRepository.findByFanIdAndArchivedIsFalse(fanId, pageRequest));

        return PageResponse
                .<PlayerIntelDTO>builder()
                .page(page)
                .size(size)
                .totalPages(result.getTotalPages())
                .totalSize(result.getTotalElements())
                .content(result.get().map(entity -> modelMapper.map(entity, PlayerIntelDTO.class)).collect(Collectors.toList()))
                .build();

    }


    public PageResponse<PlayerSquadHistoryDTO> findPlayerSquadHistory(int page, int size, Long fanId) {
        val pageRequest = PageRequest.of(page, size, Sort.by(desc("createdAt")));
        final Page<PlayerSquadHistory> result = playerSquadHistoryRepository.findByFanId(fanId, pageRequest);

        return PageResponse
                .<PlayerSquadHistoryDTO>builder()
                .page(page)
                .size(size)
                .totalPages(result.getTotalPages())
                .totalSize(result.getTotalElements())
                .content(result.get().map(entity -> modelMapper.map(entity, PlayerSquadHistoryDTO.class)).collect(Collectors.toList()))
                .build();
    }

    public boolean doAllIntelsExistAndBelongToFanId(List<Long> intelIds, Long fanId) {
        return intelIds.size() == 0 || playerIntelRepository.countByFanIdAndIdInAndArchivedIsFalse(fanId, intelIds) == intelIds.size();
    }


    public boolean playerExists(@NonNull Long fanId) {
        return this.playerRepository.existsById(fanId);
    }

    public Optional<String> updatePlayerProfileImage(@NonNull Long fanId, String profileImage) {

        return this.playerRepository.findById(fanId)
                .map(playerEntity -> {
                    playerEntity.setProfileImage(profileImage);
                    return this.playerRepository.save(playerEntity).getProfileImage();
                });

    }

    @Transactional(readOnly = true)
    public Optional<PlayerDTO> findPlayer(long fanId) {
        return playerRepository.findById(fanId)
                .map(entity -> modelMapper.map(entity, PlayerDTO.class));
    }

    public void createPlayer(CreatePlayerDTO createPlayerDTO) {

        Player player = modelMapper.map(createPlayerDTO, Player.class);

        player.setForeignMappings(Optional.ofNullable(player.getForeignMappings()).orElse(new HashSet<>()));
        if (!StreamEx.of(player.getForeignMappings()).findFirst(t -> t.getSource() == DataSourceType.INTERNAL).isPresent()) {

            player.getForeignMappings().add(new PlayerForeignMapping(
                    createPlayerDTO.getFanId(),
                    DataSourceType.INTERNAL,
                    playerCounterService.getNextCounter()
            ));
        }

        StreamEx.of(player.getEligibilities()).forEach(eligibility -> eligibility.setFanId(createPlayerDTO.getFanId()));
        StreamEx.of(player.getForeignMappings()).forEach(foreignMapping -> foreignMapping.setFanId(createPlayerDTO.getFanId()));
        StreamEx.of(player.getPlayerIntels()).forEach(playerIntel -> playerIntel.setFanId(createPlayerDTO.getFanId()));
        StreamEx.of(player.getPlayerPositions()).forEach(position -> position.setFanId(createPlayerDTO.getFanId()));
        StreamEx.of(player.getPlayerSocials()).forEach(social -> social.setFanId(createPlayerDTO.getFanId()));
        StreamEx.of(player.getPlayerSquads()).forEach(squad -> squad.setFanId(createPlayerDTO.getFanId()));

        playerRepository.save(player);

        Optional.ofNullable(player.getPlayerGrade())
                .map(grade -> {
                    PlayerGradeHistory gradeHistory = new PlayerGradeHistory();
                    gradeHistory.setFanId(createPlayerDTO.getFanId());
                    gradeHistory.setGrade(grade);
                    return gradeHistory;
                }).ifPresent(playerGradeHistoryRepository::save);

        StreamEx.of(player.getPlayerPositions())
                .map(positionDTO -> {
                    PlayerPositionHistory positionHistory = new PlayerPositionHistory();
                    positionHistory.setFanId(createPlayerDTO.getFanId());
                    positionHistory.setPositionNumber(positionDTO.getPositionNumber());
                    positionHistory.setPositionOrder(positionDTO.getPositionOrder());
                    positionHistory.setAssignment(Assignment.ADDED);
                    return positionHistory;
                }).toListAndThen(playerPositionHistoryRepository::saveAll);

        StreamEx.of(player.getPlayerSquads())
                .map(squadDTO -> {
                    PlayerSquadHistory squadHistory = new PlayerSquadHistory();
                    squadHistory.setFanId(createPlayerDTO.getFanId());
                    squadHistory.setSquad(squadDTO.getSquad());
                    squadHistory.setStatus(squadDTO.getStatus());
                    squadHistory.setAssignment(Assignment.ADDED);
                    return squadHistory;
                }).toListAndThen(playerSquadHistoryRepository::saveAll);

    }

    private void addPlayerGradeHistory(Long fanId, String playerGrade) {
        Optional.ofNullable(playerGrade)
                .map(grade -> {
                    PlayerGradeHistory gradeHistory = new PlayerGradeHistory();
                    gradeHistory.setFanId(fanId);
                    gradeHistory.setGrade(grade);
                    return gradeHistory;
                }).ifPresent(playerGradeHistoryRepository::save);
    }

    public List<PlayerShortDTO> searchPlayersWithFanIds(List<Long> fanIds) {
        return playerRepository.findAllByFanIdIn(fanIds)
                .map(entity -> modelMapper.map(entity, PlayerShortDTO.class))
                .collect(Collectors.toList());
    }

    public void editPlayer(EditPlayerDTO editPlayerDTO) {

        playerRepository.findById(editPlayerDTO.getFanId()).ifPresent(entity -> {

            updatePlayerIntels(entity, editPlayerDTO.getPlayerIntels());

            playerRepository.save(entity);

        });
    }

    private void updatePlayerIntels(Player entity, List<EditPlayerIntelDTO> intels) {

        List<Long> intelIdsFromClient = StreamEx.of(intels)
                .filter(intel -> intel.getId() != null)
                .map(EditPlayerIntelDTO::getId)
                .toList();

        List<PlayerIntel> allIntels = playerIntelRepository.findByFanId(entity.getFanId());


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
                .peek(intel -> intel.setFanId(entity.getFanId()))
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

    public void updatePlayersGrade(Map<String, Long> mapping, Set<PlayerGradeUploadDTO> playersGradePayload) {
        playersGradePayload.forEach(
                gradePayload -> {
                    Player player = playerRepository.getOne(mapping.get(gradePayload.getPlayerId()));
                    addPlayerGradeHistory(player.getFanId(), gradePayload.getGrade());
                    player.setPlayerGrade(gradePayload.getGrade());
                });
    }

    public void editPlayers(Map<String, Long> mapping, Set<PlayerStatusUploadDTO> editPlayerStatusDTOList) {
        StreamEx.of(editPlayerStatusDTOList)
                .forEach(dto -> {
                    Player player = playerRepository.getOne(mapping.get(dto.getPlayerId()));

                    if (dto.isValidMaturation()) {
                        player.setMaturationStatus(dto.getMaturationStatus());
                        player.setMaturationDate(toDate(dto.getMaturationDate()));
                    }
                    if (dto.isValidVulnerability()) {
                        player.setVulnerabilityStatus(dto.getVulnerabilityStatus());
                        player.setVulnerabilityDate(toDate(dto.getVulnerabilityDate()));
                    }
                });
    }
}
