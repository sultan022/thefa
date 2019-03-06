package com.thefa.audit.controller;

import com.google.gson.Gson;
import com.thefa.audit.config.AbstractIntegrationTest;
import com.thefa.audit.dao.bigquery.foundation.FoundationPlayersBQService;
import com.thefa.audit.dao.bigquery.opta.OptaPlayersBQService;
import com.thefa.audit.dao.bigquery.pma.PmaExternalPlayersBQService;
import com.thefa.audit.dao.bigquery.pma.PmaPlayersBQService;
import com.thefa.audit.dao.bigquery.scout7.Scout7PlayersBQService;
import com.thefa.audit.model.dto.foreign.ForeignPlayerDTO;
import com.thefa.audit.model.dto.foreign.ForeignPlayerLookupDTO;
import com.thefa.audit.model.dto.rest.fan.FanServicePlayerDTO;
import com.thefa.audit.rest.FanIdService;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static com.thefa.audit.model.shared.DataSourceType.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ForeignControllerTest extends AbstractIntegrationTest {

    private static final ForeignPlayerLookupDTO VALID_LOOKUP_REQUEST = new ForeignPlayerLookupDTO("Omer", "Arshad", null);
    private static final ForeignPlayerLookupDTO EMPTY_LOOKUP_REQUEST = new ForeignPlayerLookupDTO();

    private Gson gson = new Gson();

    @MockBean
    private OptaPlayersBQService optaPlayersBQService;

    @MockBean
    private Scout7PlayersBQService scout7PlayersBQService;

    @MockBean
    private PmaPlayersBQService pmaPlayersBQService;

    @MockBean
    private FoundationPlayersBQService foundationPlayersBQService;

    @MockBean
    private PmaExternalPlayersBQService pmaExternalPlayersBQService;

    @MockBean
    private FanIdService fanIdService;

    @Test
    public void givenInvalidRequest_whenSearchForeignPlayer_thenReturnBadRequest() throws Exception {

        mvc.perform(post("/foreign/playersSearch")
                .contentType(MediaType.APPLICATION_JSON).content(gson.toJson(EMPTY_LOOKUP_REQUEST)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void givenValidRequest_whenSearchForeignPlayer_thenReturnCorrectResult() throws Exception {

        FanServicePlayerDTO servicePlayerDTO = new FanServicePlayerDTO();
        servicePlayerDTO.setFanId(9999999L);

        ForeignPlayerDTO optaPlayer = new ForeignPlayerDTO("opta1", "Omer", "Arshad", "M", null, null, null, null, OPTA, null);
        ForeignPlayerDTO scout7Player = new ForeignPlayerDTO("opta1", "Omer", "Arshad", "M", null, null, null, null, SCOUT7, null);
        ForeignPlayerDTO pmaPlayer = new ForeignPlayerDTO("opta1", "Omer", "Arshad", "M", null, null, null, null, PMA, null);
        ForeignPlayerDTO externalPlayer = new ForeignPlayerDTO("opta1", "Omer", "Arshad", "M", null, null, null, null, PMA_EXTERNAL, null);

        when(optaPlayersBQService.findPlayers(any())).thenReturn(CompletableFuture.completedFuture(Collections.singletonList(optaPlayer)));
        when(scout7PlayersBQService.findPlayers(any())).thenReturn(CompletableFuture.completedFuture(Collections.singletonList(scout7Player)));
        when(pmaPlayersBQService.findPlayers(any())).thenReturn(CompletableFuture.completedFuture(Collections.singletonList(pmaPlayer)));
        when(fanIdService.findPlayers(any())).thenReturn(CompletableFuture.completedFuture(Collections.singletonList(servicePlayerDTO)));
        when(pmaExternalPlayersBQService.findPlayers(any())).thenReturn(CompletableFuture.completedFuture(Collections.singletonList(externalPlayer)));

        MvcResult mvcResult = mvc.perform(post("/foreign/playersSearch")
                .contentType(MediaType.APPLICATION_JSON).content(gson.toJson(VALID_LOOKUP_REQUEST)))
                .andReturn();

        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[*].source", containsInAnyOrder("OPTA", "SCOUT7", "FAN", "PMA", "PMA_EXTERNAL")));

    }

    @Test
    public void givenPlayerNotExists_whenSearchForFoundationPlayer_thenReturnExistsFlagNotSet() throws Exception {

        ForeignPlayerDTO fndPlayer = new ForeignPlayerDTO("9999999", "Omer", "Arshad", "M", null, null, null, null, INTERNAL, null);

        when(foundationPlayersBQService.findPlayers(any())).thenReturn(CompletableFuture.completedFuture(Collections.singletonList(fndPlayer)));

        MvcResult mvcResult = mvc.perform(post("/foreign/foundationPlayersSearch")
                .contentType(MediaType.APPLICATION_JSON).content(gson.toJson(VALID_LOOKUP_REQUEST)))
                .andReturn();

        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].existsPPS").doesNotExist());
    }

    @Test
    public void givenPlayerExists_whenSearchForFoundationPlayer_thenReturnExistsFlagSet() throws Exception {

        ForeignPlayerDTO fndPlayer = new ForeignPlayerDTO("1", "Omer", "Arshad", "M", null, null, null, null, INTERNAL, null);

        when(foundationPlayersBQService.findPlayers(any())).thenReturn(CompletableFuture.completedFuture(Collections.singletonList(fndPlayer)));

        MvcResult mvcResult = mvc.perform(post("/foreign/foundationPlayersSearch")
                .contentType(MediaType.APPLICATION_JSON).content(gson.toJson(VALID_LOOKUP_REQUEST)))
                .andReturn();

        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].existsPPS", is(true)));
    }

}
