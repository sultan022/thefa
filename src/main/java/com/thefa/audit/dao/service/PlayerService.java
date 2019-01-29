package com.thefa.audit.dao.service;

import com.thefa.audit.dao.repository.player.*;
import com.thefa.audit.model.dto.player.base.PlayerAttachmentDTO;
import com.thefa.audit.model.dto.player.base.PlayerDTO;
import com.thefa.audit.model.dto.player.base.PlayerIntelDTO;
import com.thefa.audit.model.dto.player.create.CreatePlayerDTO;
import com.thefa.audit.model.dto.player.edit.*;
import com.thefa.audit.model.dto.player.history.PlayerSquadHistoryDTO;
import com.thefa.audit.model.dto.player.load.PlayerGradeUploadDTO;
import com.thefa.audit.model.dto.player.load.PlayerStatusUploadDTO;
import com.thefa.audit.model.dto.player.small.PlayerShortDTO;
import com.thefa.audit.model.entity.history.PlayerGradeHistory;
import com.thefa.audit.model.entity.history.PlayerInjuryStatusHistory;
import com.thefa.audit.model.entity.history.PlayerPositionHistory;
import com.thefa.audit.model.entity.history.PlayerSquadHistory;
import com.thefa.audit.model.entity.player.*;
import com.thefa.audit.model.shared.*;
import com.thefa.common.dto.shared.PageResponse;
import com.thefa.common.util.DateUtil;
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

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.thefa.common.util.DateUtil.toDate;
import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;

@Service
@CommonsLog
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerAttachmentRepository playerAttachmentRepository;
    private final ModelMapper modelMapper;
    private final PlayerInternalMappingCounterService playerCounterService;

    private final PlayerGradeHistoryRepository playerGradeHistoryRepository;
    private final PlayerPositionHistoryRepository playerPositionHistoryRepository;
    private final PlayerSquadHistoryRepository playerSquadHistoryRepository;
    private final PlayerIntelRepository playerIntelRepository;
    private final PlayerInjuryStatusHistoryRepository playerInjuryStatusHistoryRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository,
                         PlayerGradeHistoryRepository playerGradeHistoryRepository,
                         PlayerPositionHistoryRepository playerPositionHistoryRepository,
                         PlayerSquadHistoryRepository playerSquadHistoryRepository,
                         PlayerIntelRepository playerIntelRepository,
                         PlayerInjuryStatusHistoryRepository playerInjuryStatusHistoryRepository,
                         PlayerInternalMappingCounterService playerCounterService,
                         PlayerAttachmentRepository playerAttachmentRepository,
                         ModelMapper modelMapper) {
        this.playerRepository = playerRepository;
        this.playerAttachmentRepository = playerAttachmentRepository;
        this.playerGradeHistoryRepository = playerGradeHistoryRepository;
        this.playerPositionHistoryRepository = playerPositionHistoryRepository;
        this.playerSquadHistoryRepository = playerSquadHistoryRepository;
        this.playerCounterService = playerCounterService;
        this.playerIntelRepository = playerIntelRepository;
        this.playerInjuryStatusHistoryRepository = playerInjuryStatusHistoryRepository;
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

    public boolean playersExist(Set<Long> fanIds) {
        return fanIds.size() == playerRepository.countByFanIdIn(fanIds);
    }

    public boolean playersExistWithSquadStatus(@NonNull Set<Long> fanIds, SquadType squadType) {
        return fanIds.size() == this.playerRepository.countByFanIdInAndPlayerSquadsSquadTypeContaining(fanIds, squadType);
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

    public void bulkUpdatePlayersSquads(@NonNull BulkEditPlayerSingleSquadDTO bulkEditPlayerSquadsDTO) {
        List<Player> players = playerRepository
                .findAllByFanIdIn(StreamEx.of(bulkEditPlayerSquadsDTO.getFanIds()).toList())
                .collect(Collectors.toList());

        List<PlayerSquadHistory> history = new ArrayList<>();

        StreamEx.of(players)
                .forEach(player -> {

                    Set<PlayerSquad> playerSquads = player.getPlayerSquads();

                    playerSquads.removeIf(playerSquad -> playerSquad.getSquad().equals(bulkEditPlayerSquadsDTO.getFromSquad()));
                            playerSquads.add(new PlayerSquad(player.getFanId(), bulkEditPlayerSquadsDTO.getToSquad(),
                                    bulkEditPlayerSquadsDTO.getToStatus(), null));

                            history.add(
                                    new PlayerSquadHistory(player.getFanId(), bulkEditPlayerSquadsDTO.getFromSquad(), null, Assignment.REMOVED)
                            );

                            history.add(
                                    new PlayerSquadHistory(player.getFanId(), bulkEditPlayerSquadsDTO.getToSquad(),
                                            bulkEditPlayerSquadsDTO.getToStatus(), Assignment.ADDED)
                            );

                        });

        playerRepository.saveAll(players);
        playerSquadHistoryRepository.saveAll(history);
    }

    public void bulkUpdatePlayerSquads(Set<BulkEditPlayerMultipleSquadDTO> squadDTOs) {

        List<Player> players = playerRepository
                .findAllByFanIdIn(StreamEx.of(squadDTOs).map(BulkEditPlayerMultipleSquadDTO::getFanId).toList())
                .collect(Collectors.toList());

        List<PlayerSquadHistory> history = new ArrayList<>();

        StreamEx.of(players)
                .forEach(player -> StreamEx.of(squadDTOs).findAny(dto -> dto.getFanId().equals(player.getFanId()))
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
                                        history.add(new PlayerSquadHistory(dto.getFanId(), updateSquad.getSquad(), updateSquad.getStatus(), Assignment.UPDATED));
                                    });


                            // New Squads
                            Set<EditPlayerSquadDTO> newSquads = StreamEx.of(dto.getSquads())
                                    .filter(dtoSquads -> !dbSquads.contains(dtoSquads.getSquad()))
                                    .toSet();

                            StreamEx.of(newSquads)
                                    .forEach(newSquad -> {
                                        playerSquads.add(new PlayerSquad(player.getFanId(), newSquad.getSquad(), newSquad.getStatus(), null));
                                        history.add(new PlayerSquadHistory(dto.getFanId(), newSquad.getSquad(), newSquad.getStatus(), Assignment.ADDED));
                                    });


                            // Deleted Squads
                            Set<SquadType> deletedSquads = StreamEx.of(dbSquads)
                                    .filter(dbSquad -> !uiSquads.contains(dbSquad))
                                    .toSet();

                            playerSquads.removeIf(playerSquad -> deletedSquads.contains(playerSquad.getSquad()));
                            StreamEx.of(deletedSquads)
                                    .forEach(deletedSquad -> history.add(new PlayerSquadHistory(dto.getFanId(), deletedSquad, null, Assignment.REMOVED)));

                        }));

        playerRepository.saveAll(players);
        playerSquadHistoryRepository.saveAll(history);

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
                .map(squadDTO -> new PlayerSquadHistory(createPlayerDTO.getFanId(), squadDTO.getSquad(), squadDTO.getStatus(), Assignment.ADDED))
                .toListAndThen(playerSquadHistoryRepository::saveAll);

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

    public void updatePlayerInjuryGroup(Long fanId, InjuryStatus injuryStatus) {
        playerRepository.findById(fanId)
            .ifPresent(player -> {
                    if (player.getInjuryStatus() != injuryStatus){
                         player.setInjuryStatus(injuryStatus);
                         playerInjuryStatusHistoryRepository.save(
                                 new PlayerInjuryStatusHistory(fanId, injuryStatus)
                         );
                    }
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

    public Set<PlayerForeignMapping> findPmaExternalPlayers() {
        return playerRepository.findPlayers(DataSourceType.PMA_EXTERNAL);
    }

    public Long addPlayerAttachment(@NonNull Long fanId, AttachmentType attachmentType, String attachmentPath, LocalDate campDate) {

        PlayerAttachment attachment = new PlayerAttachment();
        attachment.setAttachmentPath(attachmentPath);
        attachment.setAttachmentType(attachmentType);
        attachment.setCampDate(DateUtil.toDate(campDate));
        attachment.setFanId(fanId);
        return this.playerAttachmentRepository.save(attachment).getAttachmentId();

    }

    public Optional<PlayerAttachmentDTO> findPlayerAttachment(@NonNull Long fanId, @NonNull Long attachmentId) {
        return playerAttachmentRepository
                .findByFanIdAndAttachmentId(fanId, attachmentId)
                .map(entity -> modelMapper.map(entity, PlayerAttachmentDTO.class));
    }

    public List<PlayerAttachmentDTO> findPlayerAttachments(@NonNull Long fanId) {
        return playerAttachmentRepository
                .findAllByFanIdOrderByUploadedAtDesc(fanId)
                .map(entity -> modelMapper.map(entity, PlayerAttachmentDTO.class))
                .collect(Collectors.toList());
    }
}
