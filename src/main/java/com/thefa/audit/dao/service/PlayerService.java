package com.thefa.audit.dao.service;

import com.thefa.audit.dao.repository.player.*;
import com.thefa.audit.model.dto.player.base.PlayerAttachmentDTO;
import com.thefa.audit.model.dto.player.base.PlayerDTO;
import com.thefa.audit.model.dto.player.create.CreatePlayerDTO;
import com.thefa.audit.model.dto.player.edit.BulkEditPlayerMultipleSquadDTO;
import com.thefa.audit.model.dto.player.edit.BulkEditPlayerSingleSquadDTO;
import com.thefa.audit.model.dto.player.edit.EditPlayerDTO;
import com.thefa.audit.model.dto.player.load.PlayerGradeUploadDTO;
import com.thefa.audit.model.dto.player.load.PlayerStatusUploadDTO;
import com.thefa.audit.model.dto.player.small.PlayerShortDTO;
import com.thefa.audit.model.entity.history.PlayerGradeHistory;
import com.thefa.audit.model.entity.history.PlayerInjuryStatusHistory;
import com.thefa.audit.model.entity.history.PlayerPositionHistory;
import com.thefa.audit.model.entity.history.PlayerSquadHistory;
import com.thefa.audit.model.entity.player.Player;
import com.thefa.audit.model.entity.player.PlayerAttachment;
import com.thefa.audit.model.entity.player.PlayerForeignMapping;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.thefa.common.util.DateUtil.toDate;
import static org.springframework.data.domain.Sort.Order.asc;

@Service
@CommonsLog
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerAttachmentRepository playerAttachmentRepository;
    private final ModelMapper modelMapper;

    private final PlayerInternalMappingCounterService playerCounterService;
    private final PlayerSquadService playerSquadService;
    private final PlayerIntelService playerIntelService;

    private final PlayerGradeHistoryRepository playerGradeHistoryRepository;
    private final PlayerPositionHistoryRepository playerPositionHistoryRepository;
    private final PlayerSquadHistoryRepository playerSquadHistoryRepository;
    private final PlayerInjuryStatusHistoryRepository playerInjuryStatusHistoryRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository,
                         PlayerGradeHistoryRepository playerGradeHistoryRepository,
                         PlayerPositionHistoryRepository playerPositionHistoryRepository,
                         PlayerSquadHistoryRepository playerSquadHistoryRepository,
                         PlayerInjuryStatusHistoryRepository playerInjuryStatusHistoryRepository,
                         PlayerInternalMappingCounterService playerCounterService,
                         PlayerAttachmentRepository playerAttachmentRepository,
                         PlayerSquadService playerSquadService,
                         PlayerIntelService playerIntelService,
                         ModelMapper modelMapper) {
        this.playerRepository = playerRepository;
        this.playerAttachmentRepository = playerAttachmentRepository;
        this.playerGradeHistoryRepository = playerGradeHistoryRepository;
        this.playerPositionHistoryRepository = playerPositionHistoryRepository;
        this.playerSquadHistoryRepository = playerSquadHistoryRepository;
        this.playerCounterService = playerCounterService;
        this.playerInjuryStatusHistoryRepository = playerInjuryStatusHistoryRepository;
        this.playerSquadService = playerSquadService;
        this.playerIntelService = playerIntelService;
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

    public boolean playerExists(@NonNull String playerId) {
        return this.playerRepository.existsById(playerId);
    }

    public boolean playersExist(Set<String> playerIds) {
        return playerIds.size() == playerRepository.countByPlayerIdIn(playerIds);
    }

    public boolean playersExistWithSquadStatus(@NonNull Set<String> playerIds, SquadType squadType) {
        return playerIds.size() == this.playerRepository.countByPlayerIdInAndPlayerSquadsSquadTypeContaining(playerIds, squadType);
    }

    public Optional<String> updatePlayerProfileImage(@NonNull String playerId, String profileImage) {

        return this.playerRepository.findById(playerId)
                .map(playerEntity -> {
                    playerEntity.setProfileImage(profileImage);
                    return this.playerRepository.save(playerEntity).getProfileImage();
                });

    }

    @Transactional(readOnly = true)
    public Optional<PlayerDTO> findPlayer(String playerId) {
        return playerRepository.findById(playerId)
                .map(entity -> modelMapper.map(entity, PlayerDTO.class));
    }

    public void bulkUpdatePlayersSquads(@NonNull BulkEditPlayerSingleSquadDTO bulkEditPlayerSquadsDTO) {
        List<Player> players = playerRepository
                .findAllByPlayerIdIn(StreamEx.of(bulkEditPlayerSquadsDTO.getPlayerIds()).toList())
                .collect(Collectors.toList());

        List<PlayerSquadHistory> history = playerSquadService.bulkUpdatePlayersSquads(players, bulkEditPlayerSquadsDTO);

        playerRepository.saveAll(players);
        playerSquadHistoryRepository.saveAll(history);
    }

    public void bulkUpdatePlayerSquads(Set<BulkEditPlayerMultipleSquadDTO> squadDTOs) {

        List<Player> players = playerRepository
                .findAllByPlayerIdIn(StreamEx.of(squadDTOs).map(BulkEditPlayerMultipleSquadDTO::getPlayerId).toList())
                .collect(Collectors.toList());

        List<PlayerSquadHistory> history = playerSquadService.bulkUpdatePlayerSquads(players, squadDTOs);

        playerRepository.saveAll(players);
        playerSquadHistoryRepository.saveAll(history);

    }

    public void createPlayer(CreatePlayerDTO createPlayerDTO) {

        Player player = modelMapper.map(createPlayerDTO, Player.class);

        player.setForeignMappings(Optional.ofNullable(player.getForeignMappings()).orElse(new HashSet<>()));
        if (!StreamEx.of(player.getForeignMappings()).findFirst(t -> t.getSource() == DataSourceType.INTERNAL).isPresent()) {

            player.getForeignMappings().add(new PlayerForeignMapping(
                    createPlayerDTO.getPlayerId(),
                    DataSourceType.INTERNAL,
                    playerCounterService.getNextCounter()
            ));
        }

        StreamEx.of(player.getEligibilities()).forEach(eligibility -> eligibility.setPlayerId(createPlayerDTO.getPlayerId()));
        StreamEx.of(player.getForeignMappings()).forEach(foreignMapping -> foreignMapping.setPlayerId(createPlayerDTO.getPlayerId()));
        StreamEx.of(player.getPlayerIntels()).forEach(playerIntel -> playerIntel.setPlayerId(createPlayerDTO.getPlayerId()));
        StreamEx.of(player.getPlayerPositions()).forEach(position -> position.setPlayerId(createPlayerDTO.getPlayerId()));
        StreamEx.of(player.getPlayerSocials()).forEach(social -> social.setPlayerId(createPlayerDTO.getPlayerId()));
        StreamEx.of(player.getPlayerSquads()).forEach(squad -> squad.setPlayerId(createPlayerDTO.getPlayerId()));

        playerRepository.save(player);

        Optional.ofNullable(player.getPlayerGrade())
                .map(grade -> {
                    PlayerGradeHistory gradeHistory = new PlayerGradeHistory();
                    gradeHistory.setPlayerId(createPlayerDTO.getPlayerId());
                    gradeHistory.setGrade(grade);
                    return gradeHistory;
                }).ifPresent(playerGradeHistoryRepository::save);

        StreamEx.of(player.getPlayerPositions())
                .map(positionDTO -> {
                    PlayerPositionHistory positionHistory = new PlayerPositionHistory();
                    positionHistory.setPlayerId(createPlayerDTO.getPlayerId());
                    positionHistory.setPositionNumber(positionDTO.getPositionNumber());
                    positionHistory.setPositionOrder(positionDTO.getPositionOrder());
                    positionHistory.setAssignment(Assignment.ADDED);
                    return positionHistory;
                }).toListAndThen(playerPositionHistoryRepository::saveAll);

        StreamEx.of(player.getPlayerSquads())
                .map(squadDTO -> new PlayerSquadHistory(createPlayerDTO.getPlayerId(), squadDTO.getSquad(), squadDTO.getStatus(), Assignment.ADDED))
                .toListAndThen(playerSquadHistoryRepository::saveAll);

    }

    private void addPlayerGradeHistory(String playerId, String playerGrade) {
        Optional.ofNullable(playerGrade)
                .map(grade -> {
                    PlayerGradeHistory gradeHistory = new PlayerGradeHistory();
                    gradeHistory.setPlayerId(playerId);
                    gradeHistory.setGrade(grade);
                    return gradeHistory;
                }).ifPresent(playerGradeHistoryRepository::save);
    }

    public List<PlayerShortDTO> searchPlayersWithPlayerIds(List<String> playerIds) {
        return playerRepository.findAllByPlayerIdIn(playerIds)
                .map(entity -> modelMapper.map(entity, PlayerShortDTO.class))
                .collect(Collectors.toList());
    }

    public void editPlayer(EditPlayerDTO editPlayerDTO) {

        playerRepository.findById(editPlayerDTO.getPlayerId()).ifPresent(entity -> {

            playerIntelService.updatePlayerIntels(entity, editPlayerDTO.getPlayerIntels());

            playerRepository.save(entity);

        });
    }

    public void updatePlayersGrade(Set<PlayerGradeUploadDTO> playersGradePayload) {
        playersGradePayload.forEach(
                gradePayload -> {
                    Player player = playerRepository.getOne(gradePayload.getPlayerId());
                    addPlayerGradeHistory(player.getPlayerId(), gradePayload.getGrade());
                    player.setPlayerGrade(gradePayload.getGrade());
                });
    }

    public void updatePlayerInjuryGroup(String playerId, InjuryStatus injuryStatus) {
        playerRepository.findById(playerId)
            .ifPresent(player -> {
                    if (player.getInjuryStatus() != injuryStatus){
                         player.setInjuryStatus(injuryStatus);
                         playerInjuryStatusHistoryRepository.save(
                                 new PlayerInjuryStatusHistory(playerId, injuryStatus)
                         );
                    }
            });
    }

    public void editPlayers(Set<PlayerStatusUploadDTO> editPlayerStatusDTOList) {
        StreamEx.of(editPlayerStatusDTOList)
                .forEach(dto -> {
                    Player player = playerRepository.getOne(dto.getPlayerId());

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

    public Long addPlayerAttachment(@NonNull String playerId, AttachmentType attachmentType, String attachmentPath, LocalDate campDate) {

        PlayerAttachment attachment = new PlayerAttachment();
        attachment.setAttachmentPath(attachmentPath);
        attachment.setAttachmentType(attachmentType);
        attachment.setCampDate(DateUtil.toDate(campDate));
        attachment.setPlayerId(playerId);
        return this.playerAttachmentRepository.save(attachment).getAttachmentId();

    }

    public Optional<PlayerAttachmentDTO> findPlayerAttachment(@NonNull String playerId, @NonNull Long attachmentId) {
        return playerAttachmentRepository
                .findByPlayerIdAndAttachmentId(playerId, attachmentId)
                .map(entity -> modelMapper.map(entity, PlayerAttachmentDTO.class));
    }

    public List<PlayerAttachmentDTO> findPlayerAttachments(@NonNull String playerId) {
        return playerAttachmentRepository
                .findAllByPlayerIdOrderByUploadedAtDesc(playerId)
                .map(entity -> modelMapper.map(entity, PlayerAttachmentDTO.class))
                .collect(Collectors.toList());
    }

}
