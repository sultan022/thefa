package com.thefa.audit.controller;

import com.thefa.audit.dao.service.PlayerForeignMappingService;
import com.thefa.audit.dao.service.PlayerService;
import com.thefa.audit.dao.service.ReferenceService;
import com.thefa.audit.model.dto.player.base.PlayerAttachmentPathDTO;
import com.thefa.audit.model.dto.player.base.PlayerDTO;
import com.thefa.audit.model.dto.player.base.PlayerIntelDTO;
import com.thefa.audit.model.dto.player.create.CreatePlayerDTO;
import com.thefa.audit.model.dto.player.edit.BulkEditPlayerMultipleSquadDTO;
import com.thefa.audit.model.dto.player.edit.BulkEditPlayerSingleSquadDTO;
import com.thefa.audit.model.dto.player.edit.EditPlayerDTO;
import com.thefa.audit.model.dto.player.edit.EditPlayerIntelDTO;
import com.thefa.audit.model.dto.player.history.PlayerSquadHistoryDTO;
import com.thefa.audit.model.dto.player.load.PlayerGradeUploadDTO;
import com.thefa.audit.model.dto.player.load.PlayerStatusUploadDTO;
import com.thefa.audit.model.dto.pubsub.FndRecordUpdateMsgDTO;
import com.thefa.audit.model.shared.AttachmentType;
import com.thefa.audit.model.shared.DataSourceType;
import com.thefa.audit.model.shared.IntelType;
import com.thefa.audit.pubsub.publisher.FndPlayerUpdatedPublisher;
import com.thefa.common.cache.CacheService;
import com.thefa.common.dto.shared.ApiResponse;
import com.thefa.common.dto.shared.PageResponse;
import com.thefa.common.exception.BadRequestException;
import com.thefa.common.exception.RecordNotFoundException;
import com.thefa.common.helper.DeferredResults;
import com.thefa.common.storage.CloudStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.apachecommons.CommonsLog;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.thefa.audit.pubsub.publisher.FndPlayerUpdatedPublisher.FND_PREFIX;
import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("/players")
@CommonsLog
@Api(value = "Player", description = "Create, Edit and Search Players")
public class PlayerController {

    private final PlayerService playerService;
    private final CloudStorageService cloudStorageService;
    private final ReferenceService referenceService;
    private final PlayerForeignMappingService playerForeignMappingService;
    private final CacheService cacheService;
    private final FndPlayerUpdatedPublisher playerUpdatedPublisher;
    private final ServletContext servletContext;

    private final String cdnBucket;

    @Autowired
    public PlayerController(@Value("the-fa-api-dev") String bucket,
                            CacheService cacheService,
                            PlayerService playerService,
                            CloudStorageService cloudStorageService,
                            PlayerForeignMappingService playerForeignMappingService,
                            FndPlayerUpdatedPublisher playerUpdatedPublisher,
                            ReferenceService referenceService,
                            ServletContext servletContext) {
        this.playerService = playerService;
        this.referenceService = referenceService;
        this.cloudStorageService = cloudStorageService;
        this.cdnBucket = bucket;
        this.playerForeignMappingService = playerForeignMappingService;
        this.cacheService = cacheService;
        this.playerUpdatedPublisher = playerUpdatedPublisher;
        this.servletContext = servletContext;
    }

    @ApiOperation(value = "Upload an image for a player")
    @PostMapping(value = "/{fanId}/uploadImage", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    public DeferredResult<ApiResponse<String>> uploadFile(
            @PathVariable() long fanId,
            @RequestParam("file") MultipartFile file
    ) {

        return DeferredResults.from(
                CompletableFuture.supplyAsync(() -> {

                    if (file.isEmpty() ||
                            !Optional.ofNullable(file.getContentType())
                                    .filter(type -> type.toLowerCase().startsWith("image/")).isPresent()) {
                        throw new BadRequestException("Invalid file type");
                    }

                    validatePlayerExists(fanId);

                    return "images/" + fanId + "/" + file.getOriginalFilename();

                }).thenComposeAsync(filePath -> {
                    try {
                        return cloudStorageService.storePublicFileAsync(file.getInputStream(), file.getContentType(), cdnBucket, filePath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).thenApplyAsync(uri -> playerService.updatePlayerProfileImage(fanId, uri))
                        .thenApplyAsync(uriOption -> uriOption.map(ApiResponse::success).<RecordNotFoundException>orElseThrow(RecordNotFoundException::new))
        );
    }

    @ApiOperation(value = "Upload an attachment for a player")
    @PostMapping(value = "/{fanId}/attachments", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    public DeferredResult<ApiResponse<String>> uploadPlayerAttachment(
            @PathVariable() long fanId,
            @RequestParam(value = "campDate", required = false) LocalDate campDate,
            @RequestParam("type") AttachmentType type,
            @RequestParam("file") MultipartFile file
    ) {
        return DeferredResults.from(
                CompletableFuture.supplyAsync(() -> {

                    if (file.isEmpty() ||
                            !Optional.ofNullable(file.getContentType())
                                    .filter(contentType -> contentType.toLowerCase().equals(APPLICATION_PDF_VALUE)).isPresent()) {
                        throw new BadRequestException("Invalid file type");
                    }

                    validatePlayerExists(fanId);

                    return type.name().toLowerCase() + "/" + fanId + "/" + file.getOriginalFilename();

                }).thenComposeAsync(filePath -> {
                    try {
                        InputStream is = file.getInputStream();
                        return cloudStorageService.storePrivateFileAsync(is, file.getContentType(), cdnBucket, filePath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).thenApplyAsync(uri -> playerService.addPlayerAttachment(fanId, type, uri, campDate))
                        .thenApplyAsync(id -> servletContext.getContextPath() + "/players/" + fanId + "/attachments/" + id)
                                .thenApplyAsync(ApiResponse::success)
        );
    }

    @ApiOperation(value = "Retrieve an attachment for a player")
    @GetMapping(value = "/{fanId}/attachments/{attachmentId}", produces = APPLICATION_PDF_VALUE)
    public DeferredResult<byte[]> retrieveAttachment(
            @PathVariable() long fanId,
            @PathVariable() long attachmentId
    ) {
        return DeferredResults.from(
                playerService.findPlayerAttachment(fanId, attachmentId)
                    .map(attachment -> cloudStorageService.retrieveFileAsync(cdnBucket, attachment.getAttachmentPath())
                            .thenApplyAsync(Pair::getSecond))
                    .orElse(CompletableFuture.supplyAsync(() -> {
                        throw new RecordNotFoundException();
                    }))
        );
    }

    @GetMapping(value = "/{fanId}/attachments")
    public ApiResponse<List<PlayerAttachmentPathDTO>> playerAttachments(@PathVariable() long fanId) {
        validatePlayerExists(fanId);
        return ApiResponse.success(
                StreamEx.of(playerService.findPlayerAttachments(fanId))
                    .map(a -> new PlayerAttachmentPathDTO(
                            servletContext.getContextPath() + "/players/" + fanId + "/attachments/" + a.getAttachmentId(),
                            a.getAttachmentType(),
                            a.getCampDate(),
                            a.getUploadedBy(),
                            a.getUploadedAt()
                    ))
                    .toList()
        );
    }

    @ApiOperation(value = "Search Players")
    @GetMapping
    public ApiResponse<PageResponse<PlayerDTO>> find(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "100") int size,
            @RequestParam(value = "search", required = false) String search
    ) {
        return ApiResponse.success(playerService.findPlayers(page, size, search));
    }

    @ApiOperation(value = "Get a Player")
    @GetMapping(value = "/{fanId}")
    public ApiResponse<PlayerDTO> getPlayerDetails(@PathVariable() long fanId) {
        return playerService.findPlayer(fanId)
                .map(ApiResponse::success)
                .<RecordNotFoundException>orElseThrow(RecordNotFoundException::new);
    }

    @ApiOperation(value = "Find Player's Intel Notes By Type")
    @GetMapping(value = "/{fanId}/intels")
    public ApiResponse<PageResponse<PlayerIntelDTO>> findIntelByType(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @PathVariable() long fanId,
            @RequestParam(value = "intelType", required = false) IntelType intelType
    ) {
        validatePlayerExists(fanId);
        return ApiResponse.success(playerService.findPlayerIntels(page, size, fanId, intelType));
    }

    @ApiOperation(value = "Find Player's Squad History")
    @GetMapping(value = "/{fanId}/squadHistory")
    public ApiResponse<PageResponse<PlayerSquadHistoryDTO>> findPlayerSquadHistory(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @PathVariable() long fanId
    ) {
        validatePlayerExists(fanId);
        return ApiResponse.success(playerService.findPlayerSquadHistory(page, size, fanId));
    }

    @ApiOperation(value = "Create a new Player")
    @PostMapping
    public ApiResponse<PlayerDTO> savePlayerDetails(
            @Valid @RequestBody CreatePlayerDTO playerDTO
    ) {

        playerService.createPlayer(playerDTO);

        return playerService.findPlayer(playerDTO.getFanId()).map(player -> {

            FndRecordUpdateMsgDTO updateMsgDTO = new FndRecordUpdateMsgDTO(player.getFanId(), player, player.getCreatedAt());

            cacheService.setValue(FND_PREFIX + player.getFanId(), player.getCreatedAt(), 1, TimeUnit.DAYS);

            playerUpdatedPublisher.publish(updateMsgDTO);

            return ApiResponse.success(player);
        }).orElse(ApiResponse.success(null));

    }

    @ApiOperation(value = "Edit a Player")
    @PutMapping
    public ApiResponse<PlayerDTO> editPlayerDetails(
            @Valid @RequestBody EditPlayerDTO editPlayerDTO
    ) {

        List<Long> existingIntels = StreamEx.of(editPlayerDTO.getPlayerIntels())
                .filter(intel -> intel.getId() != null)
                .map(EditPlayerIntelDTO::getId)
                .toList();

        if (!playerService.doAllIntelsExistAndBelongToFanId(existingIntels, editPlayerDTO.getFanId())) {
            throw new BadRequestException("Invalid Intels");
        }

        playerService.editPlayer(editPlayerDTO);

        return playerService.findPlayer(editPlayerDTO.getFanId())
                .map(ApiResponse::success)
                .orElse(ApiResponse.success(null));

    }

    @ApiOperation(value = "Bulk Update Players Single Squad")
    @PutMapping(value = "/squads/single")
    public ApiResponse<Long> updatePlayerSingleSquads(@Valid @RequestBody BulkEditPlayerSingleSquadDTO bulkEditPlayerSquadsDTO) {

        if (!playerService.playersExistWithSquadStatus(bulkEditPlayerSquadsDTO.getFanIds(), bulkEditPlayerSquadsDTO.getFromSquad())) {
            throw new BadRequestException("Incorrect FanIds or Squads");
        }

        playerService.bulkUpdatePlayersSquads(bulkEditPlayerSquadsDTO);

        return ApiResponse.success((long) bulkEditPlayerSquadsDTO.getFanIds().size());
    }

    @ApiOperation(value = "Bulk Update Players Multiple Squads with Squad Statuses")
    @PutMapping(value = "/squads/multiple")
    public ApiResponse<Long> updatePlayerMultipleSquads(@Valid @RequestBody Set<BulkEditPlayerMultipleSquadDTO> bulkEditPlayerMultipleSquadDTOs) {

        if (!playerService.playersExist(StreamEx.of(bulkEditPlayerMultipleSquadDTOs).map(BulkEditPlayerMultipleSquadDTO::getFanId).toSet())) {
            throw new BadRequestException("Invalid FanIds");
        }

        playerService.bulkUpdatePlayerSquads(bulkEditPlayerMultipleSquadDTOs);

        return ApiResponse.success((long) bulkEditPlayerMultipleSquadDTOs.size());
    }

    @ApiOperation(value = "Upload Players Grades")
    @PutMapping(value = "/upload/grades")
    public ApiResponse<Long> loadPlayersGrade(@Valid @RequestBody Set<PlayerGradeUploadDTO> playersGradePayload) {

        if (!referenceService.doAllGradesExist(
                StreamEx.of(playersGradePayload).map(PlayerGradeUploadDTO::getGrade).toSet())) {
            throw new BadRequestException("There are grades which do not exist in system");
        }

        Map<String, Long> mapping = playerForeignMappingService.findPlayersFanId(DataSourceType.INTERNAL, StreamEx.of(playersGradePayload).map(PlayerGradeUploadDTO::getPlayerId).toSet());
        if (mapping.size() != playersGradePayload.size()) {
            throw new BadRequestException("There are playerIds which does not exists in system.");
        }

        playerService.updatePlayersGrade(mapping, playersGradePayload);
        return ApiResponse.success((long) playersGradePayload.size());
    }


    @ApiOperation(value = "Upload Players Statuses - Maturation Status and Vulnerability Status")
    @PutMapping("/upload/statuses")
    public ApiResponse<Long> editPlayersStatuses(
            @Valid @RequestBody Set<PlayerStatusUploadDTO> editPlayerStatusDTOSet
    ) {
        Set<PlayerStatusUploadDTO> nonEmptySet = StreamEx.of(editPlayerStatusDTOSet)
                .filter(dto -> !dto.isEmpty())
                .toSet();

        StreamEx.of(nonEmptySet)
                .findAny(dto -> !dto.isValidData())
                .ifPresent(dto -> {
                    throw new BadRequestException("One or more record are invalid");
                });

        Map<String, Long> mapping = playerForeignMappingService.findPlayersFanId(DataSourceType.INTERNAL, StreamEx.of(nonEmptySet)
                .map(PlayerStatusUploadDTO::getPlayerId).toSet());

        if (mapping.size() != nonEmptySet.size()) {
            throw new BadRequestException("There are playerIds which do not exist in system");
        }

        playerService.editPlayers(mapping, nonEmptySet);

        return ApiResponse.success((long) nonEmptySet.size());

    }

    private void validatePlayerExists(long fanId) {
        if (!playerService.playerExists(fanId)) {
            throw new RecordNotFoundException();
        }
    }

}
