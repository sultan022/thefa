package com.thefa.audit.pubsub.subscriber;

import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.Timestamp;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.thefa.audit.dao.bigquery.faraw.FndPlayersBQService;
import com.thefa.audit.model.dto.player.base.PlayerSquadDTO;
import com.thefa.audit.model.dto.pubsub.FndPlayerSquadsUpdateMsgDTO;
import com.thefa.audit.model.table.the_fa_raw.FndPlayerSquadTable;
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

import static com.thefa.audit.pubsub.publisher.PlayerSquadUpdatedPublisher.FND_SQUAD_PREFIX;

@Component
@CommonsLog
public class FndPlayerSquadSubscriber extends AbstractSubscriber<FndPlayerSquadsUpdateMsgDTO> {

    private static final String SUBSCRIPTION_NAME = "com.thefa.audit.player.squad.default";

    private final CacheService cacheService;

    private final FndPlayersBQService fndPlayersBQService;
    private final AppHelper appHelper;

    public FndPlayerSquadSubscriber(PubsubHelper pubsubHelper,
                                    CacheService cacheService,
                                    FndPlayersBQService fndPlayersBQService,
                                    AppHelper appHelper) {
        super(pubsubHelper,
                ProjectSubscriptionName.of(ServiceOptions.getDefaultProjectId(), SUBSCRIPTION_NAME),
                FndPlayerSquadsUpdateMsgDTO.class,
                InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(1).build());
        this.cacheService = cacheService;
        this.fndPlayersBQService = fndPlayersBQService;
        this.appHelper = appHelper;
    }

    @Override
    public void onReceiveMessage(FndPlayerSquadsUpdateMsgDTO message, AckReplyConsumer ack) {

        Set<PlayerSquadDTO> squads = message.getSquads();

        cacheService.getValue(FND_SQUAD_PREFIX + message.getPlayerId(), ZonedDateTime.class)
                .filter(cacheDate -> cacheDate.toInstant().equals(message.getUpdatedDate().toInstant()))
                .ifPresent(cacheDate -> {
                    if (!squads.isEmpty() && appHelper.isRunningInProd) {

                        String lastModified = Timestamp.now().toString();

                        List<FndPlayerSquadTable> squadTableRecords = StreamEx.of(squads)
                                .map(squad -> FndPlayerSquadTable.fromPlayerSquadDTO(squad, message.getPlayerId(), lastModified))
                                .toList();

                        fndPlayersBQService.addPlayerSquads(squadTableRecords)
                                .handle((table, e) -> {
                                    if (e != null) {
                                        log.error("Unable to add data to BigQuery table player squads", e);
                                    } else {
                                        log.info("Added a BigQuery Record in player squads Table for PlayerId: " + message.getPlayerId());
                                    }
                                    return table;
                                })
                                .join();
                    }
                });

        ack.ack();
    }

}
