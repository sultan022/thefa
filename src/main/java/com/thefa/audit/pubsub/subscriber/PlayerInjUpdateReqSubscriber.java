package com.thefa.audit.pubsub.subscriber;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.thefa.audit.dao.service.PlayerService;
import com.thefa.audit.model.dto.pubsub.InjRecordUpdateReqMsgDTO;
import com.thefa.audit.model.dto.rest.pma.PmaDate;
import com.thefa.audit.rest.PmaPlayerRestService;
import com.thefa.audit.service.PlayerDataSyncTriggerService;
import com.thefa.common.helper.PubsubHelper;
import com.thefa.common.pubsub.AbstractSubscriber;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

@Component
@CommonsLog
public class PlayerInjUpdateReqSubscriber extends AbstractSubscriber<InjRecordUpdateReqMsgDTO> {

    private static final String SUBSCRIPTION_NAME = "com.thefa.pma.player.req.inj.default";

    private final PlayerDataSyncTriggerService playerDataSyncTriggerService;
    private final PmaPlayerRestService pmaPlayerInjRestService;
    private final PlayerService playerService;
    public PlayerInjUpdateReqSubscriber(PubsubHelper pubsubHelper,
                                        PlayerDataSyncTriggerService playerDataSyncTriggerService,
                                        PmaPlayerRestService pmaPlayerInjRestService,
                                        PlayerService playerService) {
        super(pubsubHelper,
                ProjectSubscriptionName.of(ServiceOptions.getDefaultProjectId(), SUBSCRIPTION_NAME),
                InjRecordUpdateReqMsgDTO.class);

        this.playerDataSyncTriggerService = playerDataSyncTriggerService;
        this.pmaPlayerInjRestService = pmaPlayerInjRestService;
        this.playerService = playerService;
    }

    @Override
    public void onReceiveMessage(InjRecordUpdateReqMsgDTO injRecordUpdateMsgDTO, AckReplyConsumer ack) {

        pmaPlayerInjRestService.getPlayerTeams(injRecordUpdateMsgDTO.getPmaExternalId())
                .thenApplyAsync(player -> player.getTeamIds().stream().findFirst().orElse(0))
                .thenComposeAsync(teamId -> pmaPlayerInjRestService.getPlayerStatus(teamId, injRecordUpdateMsgDTO.getPmaExternalId()))
                .thenApplyAsync(playerStatusGroup -> {
                    playerService.updatePlayerInjuryGroup(injRecordUpdateMsgDTO.getPlayerId(), playerStatusGroup.toInjuryStatus(),
                            Optional.ofNullable(playerStatusGroup.getEstimatedReturnDate()).map(PmaDate::getIsoDate).map(str -> LocalDate.parse(str.substring(0, 10))).orElse(null));
                    playerDataSyncTriggerService.ppsDataUpdated(Collections.singleton(injRecordUpdateMsgDTO.getPlayerId()));
                    log.info("Player Injury Record Updated for Player playerId: " + injRecordUpdateMsgDTO.getPlayerId());
                    return "OK";
                })
                .join();


        ack.ack();

    }
}
