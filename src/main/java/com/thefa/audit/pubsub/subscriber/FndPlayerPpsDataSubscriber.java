package com.thefa.audit.pubsub.subscriber;

import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.Timestamp;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.thefa.audit.dao.bigquery.faraw.FndPlayersBQService;
import com.thefa.audit.model.dto.player.small.PlayerBasicDTO;
import com.thefa.audit.model.dto.pubsub.FndPlayerPpsDataUpdateMsgDTO;
import com.thefa.audit.model.table.the_fa_raw.FndPlayerPpsDataTable;
import com.thefa.common.cache.CacheService;
import com.thefa.common.helper.AppHelper;
import com.thefa.common.helper.PubsubHelper;
import com.thefa.common.pubsub.AbstractSubscriber;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Collections;

import static com.thefa.audit.pubsub.publisher.PlayerPpsDataUpdatedPublisher.FND_PPS_PREFIX;

@Component
@CommonsLog
public class FndPlayerPpsDataSubscriber extends AbstractSubscriber<FndPlayerPpsDataUpdateMsgDTO> {

    private static final String SUBSCRIPTION_NAME = "com.thefa.audit.player.pps.default";

    private final CacheService cacheService;

    private final FndPlayersBQService fndPlayersBQService;
    private final AppHelper appHelper;

    public FndPlayerPpsDataSubscriber(PubsubHelper pubsubHelper,
                                      CacheService cacheService,
                                      FndPlayersBQService fndPlayersBQService,
                                      AppHelper appHelper) {
        super(pubsubHelper,
                ProjectSubscriptionName.of(ServiceOptions.getDefaultProjectId(), SUBSCRIPTION_NAME),
                FndPlayerPpsDataUpdateMsgDTO.class,
                InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(1).build());
        this.cacheService = cacheService;
        this.fndPlayersBQService = fndPlayersBQService;
        this.appHelper = appHelper;
    }

    @Override
    public void onReceiveMessage(FndPlayerPpsDataUpdateMsgDTO message, AckReplyConsumer ack) {

        PlayerBasicDTO player = message.getData();

        cacheService.getValue(FND_PPS_PREFIX + message.getPlayerId(), ZonedDateTime.class)
                .filter(cacheDate -> cacheDate.toInstant().equals(message.getUpdatedDate().toInstant()))
                .ifPresent(cacheDate -> {
                    if (appHelper.isRunningInProd) {

                        String lastModified = Timestamp.now().toString();

                        FndPlayerPpsDataTable squadTableRecord = FndPlayerPpsDataTable.fromPlayerDTO(player, lastModified);

                        fndPlayersBQService.addPlayerPpsData(Collections.singletonList(squadTableRecord))
                                .handle((table, e) -> {
                                    if (e != null) {
                                        log.error("Unable to add data to BigQuery table player pps data", e);
                                    } else {
                                        log.info("Added a BigQuery Record in player pps data Table for PlayerId: " + message.getPlayerId());
                                    }
                                    return table;
                                })
                                .join();
                    }
                });

        ack.ack();
    }

}
