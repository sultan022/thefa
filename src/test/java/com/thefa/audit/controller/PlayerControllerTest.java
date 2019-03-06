package com.thefa.audit.controller;

import com.thefa.audit.config.AbstractIntegrationTest;
import com.thefa.audit.dao.service.PlayerService;
import com.thefa.audit.model.dto.player.base.PlayerDTO;
import com.thefa.audit.model.shared.SquadStatusType;
import com.thefa.audit.service.PlayerDataSyncTriggerService;
import com.thefa.audit.util.TestCaseUtil;
import com.thefa.common.dto.shared.SquadType;
import one.util.streamex.StreamEx;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PlayerControllerTest extends AbstractIntegrationTest {

    @SpyBean
    private PlayerService playerService;

    @MockBean
    private PlayerDataSyncTriggerService playerDataSyncTriggerService;

    @Test
    public void givenPlayers_whenGetPlayers_thenReturnCorrectJson() throws Exception {

        mvc.perform(get("/players")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    public void givenValidPlayer_whenCreatePlayer_thenSavePlayer() throws Exception {

        String json = validPlayerJsonWithPlayerId("11000");

        ArgumentCaptor<PlayerDTO> updateServicePlayer = ArgumentCaptor.forClass(PlayerDTO.class);

        mvc.perform(post("/players")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        verify(playerService).createPlayer(any());
        verify(playerDataSyncTriggerService).newPlayerCreated(updateServicePlayer.capture());

        PlayerDTO publishPlayerDTO = updateServicePlayer.getValue();
        assertEquals("Wrong playerId for published message", "11000", publishPlayerDTO.getPlayerId());

    }

    @Test
    public void givenValidPlayerWithoutId_whenCreatePlayer_thenSavePlayer() throws Exception {

        String json = validPlayerJsonWithoutPlayerId();

        ArgumentCaptor<PlayerDTO> updateServicePlayer = ArgumentCaptor.forClass(PlayerDTO.class);

        mvc.perform(post("/players")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        verify(playerService).createPlayer(any());
        verify(playerDataSyncTriggerService).newPlayerCreated(updateServicePlayer.capture());

        PlayerDTO publishPlayerDTO = updateServicePlayer.getValue();
        assertNotNull("Wrong playerId for published message", publishPlayerDTO.getPlayerId());

    }

    @Test
    public void givenPlayerAndIntelExists_whenEditPlayerWithWrongIntels_thenThrowsException() throws Exception {

        String json = validEditPlayerJsonWithPlayerId("1", 999L);

        mvc.perform(put("/players")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenPlayerAndIntelExists_whenEditPlayerIntels_thenPlayerIsEdited() throws Exception {

        String json = validEditPlayerJsonWithPlayerId("1", 1L);

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

    private static String validPlayerJsonWithoutPlayerId() {
        return "{" +
                "\"firstName\":\"Omer\"," +
                "\"lastName\":\"Arshad\"," +
                "\"dateOfBirth\":\"1970-01-01\"," +
                "\"gender\":\"M\"" +
                "}";
    }

    private static String validPlayerJsonWithPlayerId(String playerId) {
        return "{" +
                "\"playerId\": \"" + playerId + "\",\n" +
                "\"firstName\":\"Omer\"," +
                "\"lastName\":\"Arshad\"," +
                "\"dateOfBirth\":\"1970-01-01\"," +
                "\"gender\":\"M\"," +
                "\"playerGrade\":{\"grade\":\"A1\"}," +
                "\"eligibilities\":[{\"countryCode\":\"ENG\"}]," +
                "\"playerSquads\":" + "[" +
                            "{" +
                                "\"squad\":{\"squad\":\"SENIORS\"}," +
                                "\"status\":{\"status\":\"MONITOR\"}" +
                            "}" +
                        "]," +
                "\"foreignMappings\":" + "[" +
                            "{" +
                                "\"source\":\"OPTA\"," +
                                "\"foreignPlayerId\":\"dummy_id\"" +
                            "}" +
                        "]" +
                "}";
    }

    private static String validEditPlayerJsonWithPlayerId(String playerId, Long intelId) {
        return "{" +
                "\"playerId\": \"" + playerId + "\",\n" +
                "\"firstName\":\"Omer\"," +
                "\"lastName\":\"Arshad\"," +
                "\"dateOfBirth\":\"1970-01-01\"," +
                "\"gender\":\"M\"," +
                "\"playerGrade\":{\"grade\":\"A1\"}," +
                "\"eligibilities\":[{\"countryCode\":\"ENG\"}]," +
                "\"playerSquads\":" + "[" +
                "{" +
                "\"squad\":{\"squad\":\"SENIORS\"}," +
                "\"status\":{\"status\":\"MONITOR\"}" +
                "}" +
                "]," +
                "\"foreignMappings\":" + "[" +
                "{" +
                "\"source\":\"OPTA\"," +
                "\"foreignPlayerId\":\"dummy_id\"" +
                "}" +
                "]," +
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

        String validPlayerId = "1";
        String invalidPlayerId = "10";
        LocalDate murationDate = LocalDate.parse("2018-12-31");
        LocalDate vulnerabilityDate = LocalDate.parse("2018-12-31");
        String json = TestCaseUtil.editPlayersJsonWithPlayerIdsAndDates(validPlayerId, murationDate, vulnerabilityDate, invalidPlayerId);

        mvc.perform(put("/players/upload/statuses")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenPlayersAndExist_whenUploadStatuses_thenGetValidResponse() throws Exception {

        String validPlayerId1 = "1";
        String validPlayerId2 = "2";
        LocalDate murationDate = LocalDate.parse("2018-12-31");
        LocalDate vulnerabilityDate = LocalDate.parse("2018-12-31");
        String json = TestCaseUtil.editPlayersJsonWithPlayerIdsAndDates(validPlayerId1, murationDate, vulnerabilityDate, validPlayerId2);

        mvc.perform(put("/players/upload/statuses")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenPlayersWithSpacificDataAndExist_whenEditPlayers_thenProccessSpacificData() throws Exception {

        String validPlayerId1 = "1";
        String validPlayerId2 = "2";

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

        String validPlayerId1 = "1";
        String validPlayerId2 = "2";

        LocalDate murationDate = LocalDate.parse("2018-12-31");
        LocalDate vulnerabilityDate = LocalDate.parse("2018-12-31");

        String json = TestCaseUtil.editPlayersJsonWithNonEmptyData(validPlayerId1, murationDate, vulnerabilityDate, validPlayerId2);

        mvc.perform(put("/players/upload/statuses")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenPlayersWithSquads_whenBulkEditSingleSquads_thenSquadsAreUpdatedSuccessfully() throws Exception {

        String json = "{" +
                "\"playerIds\":[3,4]," +
                "\"fromSquad\":\"U21\"," +
                "\"toSquad\":\"SENIORS\"," +
                "\"toStatus\":\"MONITOR\"" +
                "}";

        Optional<PlayerDTO> player = playerService.findPlayer("3");
        assertTrue("Player Should Exist", player.isPresent());
        assertTrue("Player Squad should be present",
                StreamEx.of(player.get().getPlayerSquads()).findAny(squad -> squad.getSquadType() == SquadType.U21).isPresent());
        assertFalse("Player Squad should not be present",
                StreamEx.of(player.get().getPlayerSquads()).findAny(squad -> squad.getSquadType() == SquadType.SENIORS).isPresent());

        mvc.perform(put("/players/squads/single")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Optional<PlayerDTO> playerAfter = playerService.findPlayer("3");
        assertTrue("Player Should Exist", playerAfter.isPresent());
        assertFalse("Player Squad should not be present",
                StreamEx.of(playerAfter.get().getPlayerSquads()).findAny(squad -> squad.getSquadType() == SquadType.U21).isPresent());
        assertTrue("Player Squad should be present",
                StreamEx.of(playerAfter.get().getPlayerSquads()).findAny(squad -> squad.getSquadType() == SquadType.SENIORS &&
                        squad.getStatusType() == SquadStatusType.MONITOR).isPresent());

    }

    @Test
    public void givenPlayersWithSquads_whenBulkEditMultipleSquads_thenSquadsAreUpdatedSuccessfully() throws Exception {

        String json = "[" +
                "{" +
                "\"playerId\": 5," +
                "\"squads\": [" +
                "{" +
                "\"squad\":\"SENIORS\"," +
                "\"status\":\"MONITOR\"" +
                "}" +
                "]" +
                "}" +
                "]";

        Optional<PlayerDTO> player = playerService.findPlayer("5");
        assertTrue("Player Should Exist", player.isPresent());
        assertTrue("Player Squad should be present",
                StreamEx.of(player.get().getPlayerSquads()).findAny(squad -> squad.getSquadType() == SquadType.U21).isPresent());
        assertFalse("Player Squad should not be present",
                StreamEx.of(player.get().getPlayerSquads()).findAny(squad -> squad.getSquadType() == SquadType.SENIORS).isPresent());

        mvc.perform(put("/players/squads/multiple")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Optional<PlayerDTO> playerAfter = playerService.findPlayer("5");
        assertTrue("Player Should Exist", playerAfter.isPresent());
        assertFalse("Player Squad should not be present",
                StreamEx.of(playerAfter.get().getPlayerSquads()).findAny(squad -> squad.getSquadType() == SquadType.U21).isPresent());
        assertTrue("Player Squad should be present",
                StreamEx.of(playerAfter.get().getPlayerSquads()).findAny(squad -> squad.getSquadType() == SquadType.SENIORS).isPresent());

    }

}
