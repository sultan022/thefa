package com.thefa.audit.pubsub.subscriber;

import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.thefa.audit.dao.bigquery.faraw.FndPlayersBQService;
import com.thefa.audit.model.dto.player.base.PlayerDTO;
import com.thefa.audit.model.dto.pubsub.FndRecordUpdateMsgDTO;
import com.thefa.audit.model.table.the_fa_raw.FndPlayersTable;
import com.thefa.common.cache.CacheService;
import com.thefa.common.helper.AppHelper;
import com.thefa.common.helper.PubsubHelper;
import com.thefa.common.pubsub.AbstractSubscriber;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

import static com.thefa.audit.pubsub.publisher.FndPlayerUpdatedPublisher.FND_PREFIX;

@Component
@CommonsLog
public class FndPlayerSubscriber extends AbstractSubscriber<FndRecordUpdateMsgDTO> {

    private static final String SUBSCRIPTION_NAME = "com.thefa.audit.player.fnd.default";

    private final CacheService cacheService;

    private final FndPlayersBQService fndPlayersBQService;
    private final AppHelper appHelper;

    public FndPlayerSubscriber(PubsubHelper pubsubHelper,
                               CacheService cacheService,
                               FndPlayersBQService fndPlayersBQService,
                               AppHelper appHelper) {
        super(pubsubHelper,
                ProjectSubscriptionName.of(ServiceOptions.getDefaultProjectId(), SUBSCRIPTION_NAME),
                FndRecordUpdateMsgDTO.class,
                InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(1).build());
        this.cacheService = cacheService;
        this.fndPlayersBQService = fndPlayersBQService;
        this.appHelper = appHelper;
    }

    @Override
    public void onReceiveMessage(FndRecordUpdateMsgDTO message, AckReplyConsumer ack) {

        PlayerDTO playerDTO = message.getData();

        cacheService.getValue(FND_PREFIX + playerDTO.getPlayerId(), ZonedDateTime.class)
                .filter(cacheDate -> cacheDate.toInstant().equals(message.getUpdatedDate().toInstant()))
                .ifPresent(cacheDate -> {
                    if (appHelper.isRunningInProd) {
                        FndPlayersTable fndPlayersTable = FndPlayersTable.fromPlayerDTO(playerDTO);
                        fndPlayersBQService.addFndRecord(fndPlayersTable)
                                .handle((table, e) -> {
                                    if (e != null) {
                                        log.error("Unable to add data to BigQuery table fnd_players", e);
                                    } else {
                                        log.info("Added a BigQuery Record in fnd_players Table for PlayerId: " + fndPlayersTable.getPlayerId());
                                    }
                                    return table;
                                })
                                .join();
                    }
                });

        ack.ack();
    }

}
