package com.thefa.audit.pubsub.subscriber;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.thefa.audit.dao.service.PlayerService;
import com.thefa.audit.model.dto.pubsub.InjRecordUpdateMsgDTO;
import com.thefa.audit.rest.PmaPlayerRestService;
import com.thefa.common.helper.PubsubHelper;
import com.thefa.common.pubsub.AbstractSubscriber;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Component;

@Component
@CommonsLog
public class InjPlayerSubscriber extends AbstractSubscriber<InjRecordUpdateMsgDTO> {

    private static final String SUBSCRIPTION_NAME = "com.thefa.audit.player.inj.default";


    private final PmaPlayerRestService pmaPlayerInjRestService;
    private final PlayerService playerService;
    public InjPlayerSubscriber(PubsubHelper pubsubHelper,
                               PmaPlayerRestService pmaPlayerInjRestService,
                               PlayerService playerService) {
        super(pubsubHelper,
                ProjectSubscriptionName.of(ServiceOptions.getDefaultProjectId(), SUBSCRIPTION_NAME),
                InjRecordUpdateMsgDTO.class);

        this.pmaPlayerInjRestService = pmaPlayerInjRestService;
        this.playerService = playerService;
    }

    @Override
    public void onReceiveMessage(InjRecordUpdateMsgDTO injRecordUpdateMsgDTO, AckReplyConsumer ack) {

        pmaPlayerInjRestService.getPlayerTeams(injRecordUpdateMsgDTO.getPmaExternalId())
                .thenApplyAsync(player -> player.getTeamIds().stream().findFirst().orElse(0))
                .thenComposeAsync(teamId -> pmaPlayerInjRestService.getPlayerStatus(teamId, injRecordUpdateMsgDTO.getPmaExternalId()))
                .thenApplyAsync(playerStatusGroup -> {
                    playerService.updatePlayerInjuryGroup(injRecordUpdateMsgDTO.getPlayerId(), playerStatusGroup.toInjuryStatus());
                    log.info("Player Injury Record Updated for Player playerId: " + injRecordUpdateMsgDTO.getPlayerId());
                    return "OK";
                })
                .join();


        ack.ack();

    }
}
