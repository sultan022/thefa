package com.thefa.audit.pubsub.subscriber;

import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.Timestamp;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.thefa.audit.dao.bigquery.foundation.PlayerMappingBQService;
import com.thefa.audit.model.dto.player.base.PlayerForeignMappingDTO;
import com.thefa.audit.model.dto.pubsub.FndPlayerMappingsUpdateMsgDTO;
import com.thefa.audit.model.table.foundation.MappingPlayerIdTable;
import com.thefa.common.cache.CacheService;
import com.thefa.common.helper.AppHelper;
import com.thefa.common.helper.PubsubHelper;
import com.thefa.common.pubsub.AbstractSubscriber;
import lombok.extern.apachecommons.CommonsLog;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static com.thefa.audit.pubsub.publisher.PlayerMappingUpdatedPublisher.FND_MAPPING_PREFIX;

@Component
@CommonsLog
public class FndPlayerMappingSubscriber extends AbstractSubscriber<FndPlayerMappingsUpdateMsgDTO> {

    private static final String SUBSCRIPTION_NAME = "com.thefa.audit.player.mapping.default";

    private final CacheService cacheService;

    private final PlayerMappingBQService playerMappingBQService;
    private final AppHelper appHelper;

    public FndPlayerMappingSubscriber(PubsubHelper pubsubHelper,
                                      CacheService cacheService,
                                      PlayerMappingBQService playerMappingBQService,
                                      AppHelper appHelper) {
        super(pubsubHelper,
                ProjectSubscriptionName.of(ServiceOptions.getDefaultProjectId(), SUBSCRIPTION_NAME),
                FndPlayerMappingsUpdateMsgDTO.class,
                InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(1).build());
        this.cacheService = cacheService;
        this.playerMappingBQService = playerMappingBQService;
        this.appHelper = appHelper;
    }

    @Override
    public void onReceiveMessage(FndPlayerMappingsUpdateMsgDTO message, AckReplyConsumer ack) {

        Set<PlayerForeignMappingDTO> mappings = message.getMappings();

        cacheService.getValue(FND_MAPPING_PREFIX + message.getPlayerId(), ZonedDateTime.class)
                .filter(cacheDate -> cacheDate.toInstant().equals(message.getUpdatedDate().toInstant()))
                .ifPresent(cacheDate -> {
                    if (!mappings.isEmpty() && appHelper.isRunningInProd) {

                        String lastModified = Timestamp.now().toString();

                        List<MappingPlayerIdTable> mappingTableRecords = StreamEx.of(mappings)
                                .map(mapping -> MappingPlayerIdTable.fromPlayerMappingDTO(mapping, message.getPlayerId(), lastModified))
                                .toList();

                        playerMappingBQService.addPlayerMappings(mappingTableRecords)
                                .handle((table, e) -> {
                                    if (e != null) {
                                        log.error("Unable to add data to BigQuery table player mapping", e);
                                    } else {
                                        log.info("Added a BigQuery Record in player mapping Table for PlayerId: " + message.getPlayerId());
                                    }
                                    return table;
                                })
                                .join();
                    }
                });

        ack.ack();
    }

}
