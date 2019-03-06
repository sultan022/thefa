package com.thefa.audit.pubsub.subscriber;

import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.Timestamp;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.thefa.audit.dao.bigquery.faraw.FndPlayersBQService;
import com.thefa.audit.model.dto.player.base.PlayerPositionDTO;
import com.thefa.audit.model.dto.pubsub.FndPlayerPositionsUpdateMsgDTO;
import com.thefa.audit.model.table.the_fa_raw.FndPlayerPositionTable;
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

import static com.thefa.audit.pubsub.publisher.PlayerPositionUpdatedPublisher.FND_POSITION_PREFIX;

@Component
@CommonsLog
public class FndPlayerPositionSubscriber extends AbstractSubscriber<FndPlayerPositionsUpdateMsgDTO> {

    private static final String SUBSCRIPTION_NAME = "com.thefa.audit.player.position.default";

    private final CacheService cacheService;

    private final FndPlayersBQService fndPlayersBQService;
    private final AppHelper appHelper;

    public FndPlayerPositionSubscriber(PubsubHelper pubsubHelper,
                                       CacheService cacheService,
                                       FndPlayersBQService fndPlayersBQService,
                                       AppHelper appHelper) {
        super(pubsubHelper,
                ProjectSubscriptionName.of(ServiceOptions.getDefaultProjectId(), SUBSCRIPTION_NAME),
                FndPlayerPositionsUpdateMsgDTO.class,
                InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(1).build());
        this.cacheService = cacheService;
        this.fndPlayersBQService = fndPlayersBQService;
        this.appHelper = appHelper;
    }

    @Override
    public void onReceiveMessage(FndPlayerPositionsUpdateMsgDTO message, AckReplyConsumer ack) {

        Set<PlayerPositionDTO> positions = message.getPositions();

        cacheService.getValue(FND_POSITION_PREFIX + message.getPlayerId(), ZonedDateTime.class)
                .filter(cacheDate -> cacheDate.toInstant().equals(message.getUpdatedDate().toInstant()))
                .ifPresent(cacheDate -> {
                    if (!positions.isEmpty() && appHelper.isRunningInProd) {

                        String lastModified = Timestamp.now().toString();

                        List<FndPlayerPositionTable> positionTableRecords = StreamEx.of(positions)
                                .map(position -> FndPlayerPositionTable.fromPlayerPositionDTO(position, message.getPlayerId(), lastModified))
                                .toList();

                        fndPlayersBQService.addPlayerPositions(positionTableRecords)
                                .handle((table, e) -> {
                                    if (e != null) {
                                        log.error("Unable to add data to BigQuery table player positions", e);
                                    } else {
                                        log.info("Added a BigQuery Record in player positions Table for PlayerId: " + message.getPlayerId());
                                    }
                                    return table;
                                })
                                .join();
                    }
                });

        ack.ack();
    }

}
