package com.thefa.audit.pubsub.suscriber;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.thefa.audit.config.AbstractIntegrationTest;
import com.thefa.audit.dao.service.PlayerService;
import com.thefa.audit.model.dto.player.base.PlayerDTO;
import com.thefa.audit.model.dto.pubsub.InjRecordUpdateMsgDTO;
import com.thefa.audit.model.dto.rest.pma.PmaPlayerInjuryStatusGroupDTO;
import com.thefa.audit.model.dto.rest.pma.PmaPlayerInjuryTeamDTO;
import com.thefa.audit.model.shared.InjuryStatus;
import com.thefa.audit.pubsub.subscriber.InjPlayerSubscriber;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InjPlayerSubscriberTest extends AbstractIntegrationTest {


    @Autowired
    private PlayerService playerService;


    @Autowired
    InjPlayerSubscriber injPlayerSubscriber;


    @Test
    public void givenCorrectPlayerExternalId_playerInjuryStatusIsUpdated() {

        Optional<PlayerDTO> player = playerService.findPlayer("5");
        final AckReplyConsumer ack = mock(AckReplyConsumer.class);

        assertTrue("Player Should Exist", player.isPresent());
        assertNull("Player Injury Status Should not be present", player.get().getInjuryStatus());

        InjRecordUpdateMsgDTO injRecordUpdateMsgDTO = new InjRecordUpdateMsgDTO("5", "1084313");

        PmaPlayerInjuryTeamDTO pmaPlayerInjuryTeamDTO = new PmaPlayerInjuryTeamDTO(Arrays.asList(1011386));
        PmaPlayerInjuryStatusGroupDTO pmaPlayerInjuryStatusGroupDTO = new PmaPlayerInjuryStatusGroupDTO("Fit");

        when(pmaPlayerRestService.getPlayerStatus(any(), any())).thenReturn(CompletableFuture.completedFuture(pmaPlayerInjuryStatusGroupDTO));
        when(pmaPlayerRestService.getPlayerTeams(any())).thenReturn(CompletableFuture.completedFuture(pmaPlayerInjuryTeamDTO));

        injPlayerSubscriber.onReceiveMessage(injRecordUpdateMsgDTO, ack);

        player = playerService.findPlayer("5");
        assertTrue("Player Should Exist", player.isPresent());
        assertEquals("Player Injury Status Should be Updated", InjuryStatus.GREEN, player.get().getInjuryStatus());
        verify(ack, times(1)).ack();


    }


}
