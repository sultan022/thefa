package com.thefa.audit.controller;

import com.thefa.audit.config.AbstractIntegrationTest;
import com.thefa.audit.dao.service.PlayerService;
import com.thefa.audit.model.dto.player.base.PlayerDTO;
import com.thefa.audit.model.dto.pubsub.FndRecordUpdateMsgDTO;
import com.thefa.audit.pubsub.publisher.FndPlayerUpdatedPublisher;
import com.thefa.audit.util.TestCaseUtil;
import com.thefa.common.cache.CacheService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static com.thefa.audit.pubsub.publisher.FndPlayerUpdatedPublisher.FND_PREFIX;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PlayerControllerTest extends AbstractIntegrationTest {

    @SpyBean
    private CacheService cacheService;

    @SpyBean
    private PlayerService playerService;

    @SpyBean
    private FndPlayerUpdatedPublisher playerUpdatedPublisher;

    @Test
    public void givenPlayers_whenGetPlayers_thenReturnCorrectJson() throws Exception {

        mvc.perform(get("/players")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content[0].fanId", is(1)))
                .andExpect(jsonPath("$.data.content[0].firstName", is("Nayyer")));
    }

    @Test
    public void givenValidPlayer_whenCreatePlayer_thenSavePlayer() throws Exception {

        String json = validPlayerJsonWithFanId(11000L);

        ArgumentCaptor<FndRecordUpdateMsgDTO> fndMsgPublish = ArgumentCaptor.forClass(FndRecordUpdateMsgDTO.class);

        mvc.perform(post("/players")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.foreignMappings[0].source", is("INTERNAL")));

        verify(playerService).createPlayer(any());
        verify(cacheService).setValue(eq(FND_PREFIX + 11000), notNull(), eq(1L), eq(TimeUnit.DAYS));
        verify(playerUpdatedPublisher).publish(fndMsgPublish.capture());

        PlayerDTO publishPlayerDTO = fndMsgPublish.getValue().getData();
        assertEquals("Wrong fanId for published message", Long.valueOf(11000L), publishPlayerDTO.getFanId());

    }

    @Test
    public void givenPlayerAndIntelExists_whenEditPlayerWithWrongIntels_thenThrowsException() throws Exception {

        String json = validEditPlayerJsonWithFanId(1L, 999L);

        mvc.perform(put("/players")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenPlayerAndIntelExists_whenEditPlayerIntels_thenPlayerIsEdited() throws Exception {

        String json = validEditPlayerJsonWithFanId(1L, 1L);

        mvc.perform(put("/players")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenPlayerExists_whenRequestPlayerSquadHistory_thenGetValidResponse() throws Exception {
        mvc.perform(get("/players/1/squadHistory")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    public void givenPlayerNotExists_whenRequestPlayerSquadHistory_thenGet404Error() throws Exception {
        mvc.perform(get("/players/999/squadHistory")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private static String validPlayerJsonWithFanId(Long fanId) {
        return "{" +
                "\"fanId\":" + fanId + "," +
                "\"firstName\":\"Omer\"," +
                "\"lastName\":\"Arshad\"," +
                "\"dateOfBirth\":\"1970-01-01\"," +
                "\"gender\":\"M\"" +
                "}";
    }

    private static String validEditPlayerJsonWithFanId(Long fanId, Long intelId) {
        return "{" +
                "\"fanId\":" + fanId + "," +
                "\"playerIntels\": [" +
                "{" +
                "\"id\":" + intelId + "," +
                "\"intelType\":\"GENERAL\"," +
                "\"note\":\"Some Modified Note\"" +
                "}" +
                "]" +
                "}";
    }

    @Test
    public void givenPlayers_firstNotExist_whenEditPlayers_thenThrowsException() throws Exception {

        String validPlayerId = "fapl0001";
        String invalidPlayerId = "opta1000";
        LocalDate murationDate = LocalDate.parse("2018-12-31");
        LocalDate vulnerabilityDate = LocalDate.parse("2018-12-31");
        String json = TestCaseUtil.editPlayersJsonWithPlayerIdsAndDates(validPlayerId, murationDate, vulnerabilityDate, invalidPlayerId);

        mvc.perform(put("/players/upload/statuses")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenPlayersAndExist_whenEditPlayers_thenGetValidResponse() throws Exception {

        String validPlayerId1 = "fapl0001";
        String validPlayerId2 = "fapl0002";
        LocalDate murationDate = LocalDate.parse("2018-12-31");
        LocalDate vulnerabilityDate = LocalDate.parse("2018-12-31");
        String json = TestCaseUtil.editPlayersJsonWithPlayerIdsAndDates(validPlayerId1, murationDate, vulnerabilityDate, validPlayerId2);

        mvc.perform(put("/players/upload/statuses")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenPlayersWithInValidAndExist_whenEditPlayers_thenThrowsException() throws Exception {

        String validPlayerId1 = "fapl0001";
        String json = TestCaseUtil.editPlayersJsonWithEmptyData(validPlayerId1);

        mvc.perform(put("/players/upload/statuses")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenPlayersWithOnePlayerEmptyDateAndExist_whenEditPlayers_thenThrowsException() throws Exception {

        String validPlayerId1 = "fapl0001";
        String validPlayerId2 = "fapl0002";

        LocalDate murationDate = LocalDate.parse("2018-12-31");
        LocalDate vulnerabilityDate = LocalDate.parse("2018-12-31");

        String json = TestCaseUtil.editPlayersJsonWithPlayerIdsAndEmptyData(validPlayerId1, murationDate, vulnerabilityDate, validPlayerId2);


        mvc.perform(put("/players/upload/statuses")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenPlayersWithSpacificDataAndExist_whenEditPlayers_thenProccessSpacificData() throws Exception {

        String validPlayerId1 = "fapl0001";
        String validPlayerId2 = "fapl0002";
//
        LocalDate murationDate = LocalDate.parse("2018-12-31");
        LocalDate vulnerabilityDate = LocalDate.parse("2018-12-31");

        String json = TestCaseUtil.editPlayersJsonWithPlayerIdsAndSpacificData(validPlayerId1, murationDate, vulnerabilityDate, validPlayerId2);

        mvc.perform(put("/players/upload/statuses")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenPlayersWithOneEmptyDataAndExist_whenEditPlayers_thenProccessNonEmpty() throws Exception {

        String validPlayerId1 = "fapl0001";
        String validPlayerId2 = "fapl0002";

        LocalDate murationDate = LocalDate.parse("2018-12-31");
        LocalDate vulnerabilityDate = LocalDate.parse("2018-12-31");

        String json = TestCaseUtil.editPlayersJsonWithNonEmptyData(validPlayerId1, murationDate, vulnerabilityDate, validPlayerId2);

        mvc.perform(put("/players/upload/statuses")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
