package com.thefa.audit.dao.service;

import com.thefa.audit.dao.repository.player.*;
import com.thefa.audit.model.dto.player.base.PlayerAttachmentDTO;
import com.thefa.audit.model.dto.player.base.PlayerDTO;
import com.thefa.audit.model.dto.player.base.PlayerGradeDTO;
import com.thefa.audit.model.dto.player.base.PlayerImagesDTO;
import com.thefa.audit.model.dto.player.create.CreatePlayerDTO;
import com.thefa.audit.model.dto.player.edit.BulkEditPlayerMultipleSquadDTO;
import com.thefa.audit.model.dto.player.edit.BulkEditPlayerSingleSquadDTO;
import com.thefa.audit.model.dto.player.edit.EditPlayerDTO;
import com.thefa.audit.model.dto.player.load.PlayerGradeUploadDTO;
import com.thefa.audit.model.dto.player.load.PlayerStatusUploadDTO;
import com.thefa.audit.model.dto.player.small.PlayerBasicDTO;
import com.thefa.audit.model.entity.history.PlayerGradeHistory;
import com.thefa.audit.model.entity.history.PlayerInjuryStatusHistory;
import com.thefa.audit.model.entity.history.PlayerPositionHistory;
import com.thefa.audit.model.entity.history.PlayerSquadHistory;
import com.thefa.audit.model.entity.player.Player;
import com.thefa.audit.model.entity.player.PlayerAttachment;
import com.thefa.audit.model.entity.player.PlayerEligibility;
import com.thefa.audit.model.entity.reference.Club;
import com.thefa.audit.model.entity.reference.Country;
import com.thefa.audit.model.entity.reference.Grade;
import com.thefa.audit.model.shared.Assignment;
import com.thefa.audit.model.shared.AttachmentType;
import com.thefa.audit.model.shared.InjuryStatus;
import com.thefa.common.dto.shared.PageResponse;
import com.thefa.common.dto.shared.SquadType;
import com.thefa.common.util.DateUtil;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import lombok.val;
import one.util.streamex.StreamEx;
import org.apache.commons.codec.binary.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    private final PlayerPositionService playerPositionService;

    private final ReferenceService referenceService;

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
                         PlayerPositionService playerPositionService,
                         ReferenceService referenceService,
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
        this.playerPositionService = playerPositionService;
        this.referenceService = referenceService;
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
        return playerIds.size() == this.playerRepository.countByPlayerIdInAndPlayerSquadsSquadTypeContaining(playerIds, squadType.name());
    }

    public Optional<PlayerImagesDTO> updatePlayerProfileImage(@NonNull String playerId, List<String> imageList) {

        return this.playerRepository.findById(playerId)
                .map(playerEntity -> {
                    playerEntity.setProfileImage(imageList.get(0));
                    playerEntity.setThumbnailImage(imageList.get(1));
                    playerRepository.save(playerEntity);

                    return new PlayerImagesDTO(playerEntity.getProfileImage(), playerEntity.getThumbnailImage());


                });

    }

    @Transactional(readOnly = true)
    public Optional<PlayerDTO> findPlayer(String playerId) {
        return playerRepository.findById(playerId)
                .map(entity -> modelMapper.map(entity, PlayerDTO.class));
    }

    @Transactional(readOnly = true)
    public List<PlayerBasicDTO> getPlayersBasicDetails(Set<String> playerIds) {
        return playerRepository
                .findAllByPlayerIdIn(playerIds)
                .map(player -> modelMapper.map(player, PlayerBasicDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PlayerBasicDTO> getPlayerBasicDetail(String playerId) {
        return playerRepository
                .findById(playerId)
                .map(player -> modelMapper.map(player, PlayerBasicDTO.class));
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

        if (createPlayerDTO.getPlayerId() == null) {
            createPlayerDTO.setPlayerId(playerCounterService.getNextCounter());
        }

        Player player = modelMapper.map(createPlayerDTO, Player.class);

        StreamEx.of(player.getEligibilities()).forEach(eligibility -> {
            eligibility.setPlayerId(createPlayerDTO.getPlayerId());
            eligibility.setCountry(referenceService.findCountry(eligibility.getCountryCode()).orElse(null));
            eligibility.setPlayer(player);
        });
        StreamEx.of(player.getForeignMappings()).forEach(foreignMapping -> {
            foreignMapping.setPlayerId(createPlayerDTO.getPlayerId());
            foreignMapping.setPlayer(player);
        });
        StreamEx.of(player.getPlayerIntels()).forEach(playerIntel -> playerIntel.setPlayerId(createPlayerDTO.getPlayerId()));
        StreamEx.of(player.getPlayerPositions()).forEach(position -> {
            position.setPlayerId(createPlayerDTO.getPlayerId());
            position.setPlayer(player);
        });
        StreamEx.of(player.getPlayerSocials()).forEach(social -> {
            social.setPlayerId(createPlayerDTO.getPlayerId());
            social.setPlayer(player);
        });
        StreamEx.of(player.getPlayerSquads()).forEach(squad -> {
            squad.setPlayerId(createPlayerDTO.getPlayerId());
            squad.setSquad(referenceService.findSquad(squad.getSquadType()).orElse(null));
            squad.setPlayer(player);
        });

        playerRepository.save(player);

        Optional.ofNullable(player.getPlayerGrade())
                .map(grade -> {
                    PlayerGradeHistory gradeHistory = new PlayerGradeHistory();
                    gradeHistory.setPlayerId(createPlayerDTO.getPlayerId());
                    gradeHistory.setGrade(grade.getGrade());
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
                .map(squadDTO -> new PlayerSquadHistory(createPlayerDTO.getPlayerId(), squadDTO.getSquadType(), squadDTO.getStatusType(), Assignment.ADDED))
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

    public void editPlayer(EditPlayerDTO editPlayerDTO) {

        playerRepository.findById(editPlayerDTO.getPlayerId()).ifPresent(entity -> {

            entity.setFirstName(editPlayerDTO.getFirstName());
            entity.setMiddleName(editPlayerDTO.getMiddleName());
            entity.setLastName(editPlayerDTO.getLastName());
            entity.setKnownName(editPlayerDTO.getKnownName());
            entity.setDateOfBirth(DateUtil.toDate(editPlayerDTO.getDateOfBirth()));
            entity.setGender(editPlayerDTO.getGender());
            entity.setClub(Optional.ofNullable(editPlayerDTO.getClub()).map(clubDTO -> new Club(clubDTO.getId())).orElse(null));

            entity.getEligibilities().removeIf(playerEligibility -> !StreamEx.of(editPlayerDTO.getEligibilities())
                                                                        .findAny(p -> p.getCountryCode().equals(playerEligibility.getCountryCode())).isPresent());
            Set<String> existingCountries = StreamEx.of(entity.getEligibilities()).map(PlayerEligibility::getCountry).map(Country::getCountryCode).toSet();
            StreamEx.of(editPlayerDTO.getEligibilities())
                    .filter(countryDTO -> !existingCountries.contains(countryDTO.getCountryCode()))
                    .map(country -> new PlayerEligibility(editPlayerDTO.getPlayerId(),
                            referenceService.findCountry(country.getCountryCode()).orElse(null),
                            null))
                    .forEach(entity.getEligibilities()::add);

            playerSquadService.updatePlayerSquads(entity, editPlayerDTO.getPlayerSquads());
            playerPositionService.updatePlayerPositions(entity, editPlayerDTO.getPlayerPositions());

            String editGrade = Optional.ofNullable(editPlayerDTO.getPlayerGrade()).map(PlayerGradeDTO::getGrade).orElse(null);
            if (!StringUtils.equals(
                    Optional.ofNullable(entity.getPlayerGrade()).map(Grade::getGrade).orElse(null),
                    editGrade)
            ) {
                addPlayerGradeHistory(editPlayerDTO.getPlayerId(), editGrade);
                entity.setPlayerGrade(Optional.ofNullable(editGrade).map(g -> new Grade(g, null)).orElse(null));
            }

            playerIntelService.updatePlayerIntels(entity, editPlayerDTO.getPlayerIntels());

            playerRepository.save(entity);

        });
    }

    public void updatePlayersGrade(Set<PlayerGradeUploadDTO> playersGradePayload) {
        playersGradePayload.forEach(
                gradePayload -> {
                    Player player = playerRepository.getOne(gradePayload.getPlayerId());
                    if (!StringUtils.equals(
                            Optional.ofNullable(player.getPlayerGrade()).map(Grade::getGrade).orElse(null),
                            gradePayload.getGrade())) {
                        addPlayerGradeHistory(player.getPlayerId(), gradePayload.getGrade());
                        player.setPlayerGrade(new Grade(gradePayload.getGrade(), null));
                    }
                });
    }

    public void updatePlayerInjuryGroup(String playerId, InjuryStatus injuryStatus, LocalDate expectedReturnDate) {
        playerRepository.findById(playerId)
                .ifPresent(player -> {
                    player.setInjuryStatus(injuryStatus);
                    player.setExpectedReturnDate(
                            Optional.ofNullable(expectedReturnDate).map(DateUtil::toDate).orElse(null)
                    );
                    if (player.getInjuryStatus() != injuryStatus) {
                        playerInjuryStatusHistoryRepository.save(
                                new PlayerInjuryStatusHistory(playerId, injuryStatus)
                        );
                    }
                });
    }

    public void updateVulnerabilityStatus(Set<PlayerStatusUploadDTO> editPlayerStatusDTOList) {
        
        StreamEx.of(editPlayerStatusDTOList)
                .forEach(dto -> {
                    Player player = playerRepository.getOne(dto.getPlayerId());
                    player.setMaturationStatus(dto.getMaturationStatus());
                    player.setMaturationDate(toDate(dto.getMaturationDate()));
                    player.setVulnerabilityStatus(dto.getVulnerabilityStatus());
                    player.setVulnerabilityDate(toDate(dto.getVulnerabilityDate()));
                    player.setVulnerabilityStatus4Weeks(dto.getVulnerabilityStatus4Weeks());
                    player.setVulnerabilityStatus8Weeks(dto.getVulnerabilityStatus8Weeks());
                    player.setVulnerabilityStatus12Weeks(dto.getVulnerabilityStatus12Weeks());

                    playerRepository.save(player);
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

    public void updatePlayerProfileAndThumbnailImage(Map imageUriMap, String playerId) {
    }
}
