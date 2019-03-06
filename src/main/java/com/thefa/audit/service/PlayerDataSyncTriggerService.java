package com.thefa.audit.service;

import com.thefa.audit.dao.service.PlayerService;
import com.thefa.audit.dao.service.PlayerSquadService;
import com.thefa.audit.dao.service.datastore.PlayerDatastoreService;
import com.thefa.audit.dao.service.datastore.PlayerSummaryDatastoreService;
import com.thefa.audit.model.dto.player.base.PlayerDTO;
import com.thefa.audit.model.dto.player.base.PlayerSquadDTO;
import com.thefa.audit.model.dto.player.small.PlayerBasicDTO;
import com.thefa.audit.model.dto.player.specific.CompletePlayerSquadDTO;
import com.thefa.audit.model.dto.pubsub.*;
import com.thefa.audit.pubsub.publisher.*;
import com.thefa.common.cache.CacheService;
import one.util.streamex.StreamEx;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.thefa.audit.pubsub.publisher.FndPlayerUpdatedPublisher.FND_PREFIX;
import static com.thefa.audit.pubsub.publisher.PlayerMappingUpdatedPublisher.FND_MAPPING_PREFIX;
import static com.thefa.audit.pubsub.publisher.PlayerPositionUpdatedPublisher.FND_POSITION_PREFIX;
import static com.thefa.audit.pubsub.publisher.PlayerPpsDataUpdatedPublisher.FND_PPS_PREFIX;
import static com.thefa.audit.pubsub.publisher.PlayerSquadUpdatedPublisher.FND_SQUAD_PREFIX;

@Service
public class PlayerDataSyncTriggerService {

    private final FndPlayerUpdatedPublisher fndPlayerUpdatedPublisher;
    private final PlayerSquadUpdatedPublisher playerSquadUpdatedPublisher;
    private final PlayerPositionUpdatedPublisher playerPositionUpdatedPublisher;
    private final PlayerMappingUpdatedPublisher playerMappingUpdatedPublisher;
    private final PlayerPpsDataUpdatedPublisher playerPpsDataUpdatedPublisher;

    private final PlayerService playerService;
    private final PlayerSquadService playerSquadService;
    private final PlayerSummaryDatastoreService playerSummaryDatastoreService;
    private final PlayerDatastoreService playerDatastoreService;

    private final CacheService cacheService;

    private final ModelMapper modelMapper;

    public PlayerDataSyncTriggerService(FndPlayerUpdatedPublisher fndPlayerUpdatedPublisher,
                                        PlayerSquadUpdatedPublisher playerSquadUpdatedPublisher,
                                        PlayerPositionUpdatedPublisher playerPositionUpdatedPublisher,
                                        PlayerMappingUpdatedPublisher playerMappingUpdatedPublisher,
                                        PlayerPpsDataUpdatedPublisher playerPpsDataUpdatedPublisher,
                                        PlayerService playerService,
                                        PlayerSummaryDatastoreService playerSummaryDatastoreService,
                                        PlayerDatastoreService playerDatastoreService,
                                        PlayerSquadService playerSquadService,
                                        CacheService cacheService,
                                        ModelMapper modelMapper) {

        this.fndPlayerUpdatedPublisher = fndPlayerUpdatedPublisher;
        this.playerSquadUpdatedPublisher = playerSquadUpdatedPublisher;
        this.playerPositionUpdatedPublisher = playerPositionUpdatedPublisher;
        this.playerMappingUpdatedPublisher = playerMappingUpdatedPublisher;
        this.playerPpsDataUpdatedPublisher = playerPpsDataUpdatedPublisher;

        this.playerService = playerService;
        this.playerSquadService = playerSquadService;
        this.playerSummaryDatastoreService = playerSummaryDatastoreService;
        this.playerDatastoreService = playerDatastoreService;

        this.cacheService = cacheService;
        this.modelMapper = modelMapper;

    }

    public void newPlayerCreated(PlayerDTO player) {
        playerDatastoreService.createPlayerKindFromDTO(player);
        playerSummaryDatastoreService.createPlayerSummaryFromDTO(player);
        triggerCompletePlayerUpdated(player, player.getCreatedAt());
    }

    public void playerUpdated(PlayerDTO player) {
        playerDatastoreService.updatePlayerKindFromDTO(player);
        playerSummaryDatastoreService.updatePlayerSummaryFromDTO(player);
        triggerCompletePlayerUpdated(player, player.getUpdatedAt());
    }

    public void fndDataUpdated(String playerId) {

        playerService.findPlayer(playerId)
                .ifPresent(player -> {

                    ZonedDateTime updatedAt = player.getUpdatedAt();

                    FndRecordUpdateMsgDTO updateMsgDTO = new FndRecordUpdateMsgDTO(playerId, player, updatedAt);

                    cacheService.setValue(FND_PREFIX + playerId, updatedAt, 1, TimeUnit.DAYS);

                    fndPlayerUpdatedPublisher.publish(updateMsgDTO);
                    updatePlayersDataStore(playerId);
                });


    }

    public void squadsUpdated(Set<String> playerIds) {

        ZonedDateTime now = ZonedDateTime.now();

        StreamEx.of(playerSquadService.findAllByPlayerIdIn(playerIds))
                .groupingBy(CompletePlayerSquadDTO::getPlayerId)
                .forEach((playerId, group) -> {
                    FndPlayerSquadsUpdateMsgDTO squadsMsgDTO =
                            new FndPlayerSquadsUpdateMsgDTO(playerId, StreamEx.of(group).map(dto -> modelMapper.map(dto, PlayerSquadDTO.class)).toSet(), now);

                    cacheService.setValue(FND_SQUAD_PREFIX + playerId, now, 1, TimeUnit.DAYS);

                    playerSquadUpdatedPublisher.publish(squadsMsgDTO);
                    updatePlayersDataStore(playerId);
                });

    }

    public void ppsDataUpdated(Set<String> playerIds) {

        ZonedDateTime now = ZonedDateTime.now();

        StreamEx.of(playerService.getPlayersBasicDetails(playerIds))
            .forEach(player -> {

                FndPlayerPpsDataUpdateMsgDTO ppsMsgDTO = new FndPlayerPpsDataUpdateMsgDTO(player.getPlayerId(), player, now);

                cacheService.setValue(FND_PPS_PREFIX + player.getPlayerId(), now, 1, TimeUnit.DAYS);

                playerPpsDataUpdatedPublisher.publish(ppsMsgDTO);
                updatePlayersDataStore(player.getPlayerId());
            });
    }

    private void triggerCompletePlayerUpdated(PlayerDTO player, ZonedDateTime updated) {
        String playerId = player.getPlayerId();

        FndRecordUpdateMsgDTO updateMsgDTO = new FndRecordUpdateMsgDTO(playerId, player, updated);
        FndPlayerSquadsUpdateMsgDTO squadsMsgDTO = new FndPlayerSquadsUpdateMsgDTO(playerId, player.getPlayerSquads(), updated);
        FndPlayerPositionsUpdateMsgDTO positionsMsgDTO = new FndPlayerPositionsUpdateMsgDTO(playerId, player.getPlayerPositions(), updated);
        FndPlayerMappingsUpdateMsgDTO mappingsMsgDTO = new FndPlayerMappingsUpdateMsgDTO(playerId, player.getForeignMappings(), updated);
        FndPlayerPpsDataUpdateMsgDTO ppsMsgDTO = new FndPlayerPpsDataUpdateMsgDTO(playerId,
                modelMapper.map(player, PlayerBasicDTO.class), updated);

        cacheService.setValue(FND_PREFIX + playerId, updated, 1, TimeUnit.DAYS);
        cacheService.setValue(FND_SQUAD_PREFIX + playerId, updated, 1, TimeUnit.DAYS);
        cacheService.setValue(FND_POSITION_PREFIX + playerId, updated, 1, TimeUnit.DAYS);
        cacheService.setValue(FND_MAPPING_PREFIX + playerId, updated, 1, TimeUnit.DAYS);
        cacheService.setValue(FND_PPS_PREFIX + playerId, updated, 1, TimeUnit.DAYS);

        fndPlayerUpdatedPublisher.publish(updateMsgDTO);
        playerSquadUpdatedPublisher.publish(squadsMsgDTO);
        playerPositionUpdatedPublisher.publish(positionsMsgDTO);
        playerMappingUpdatedPublisher.publish(mappingsMsgDTO);
        playerPpsDataUpdatedPublisher.publish(ppsMsgDTO);
    }

    private void updatePlayersDataStore(String playerId) {
        playerService.findPlayer(playerId)
                .ifPresent(player -> {
                    playerDatastoreService.updatePlayerKindFromDTO(player);
                    playerSummaryDatastoreService.updatePlayerSummaryFromDTO(player);
                });
    }
}
