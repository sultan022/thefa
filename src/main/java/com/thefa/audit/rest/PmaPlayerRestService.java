package com.thefa.audit.rest;

import com.thefa.audit.model.dto.rest.pma.PmaPlayerInjAuthTokenDTO;
import com.thefa.audit.model.dto.rest.pma.PmaPlayerInjuryStatusGroupDTO;
import com.thefa.audit.model.dto.rest.pma.PmaPlayerInjuryTeamDTO;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@CommonsLog
public class PmaPlayerRestService {

    private final static URI PMA_INJ_PLAYERS_AUTH_URI = URI.create("https://pmaapi.premierleague.com/api/External/oauth2/token");
    private final static URI PMA_INJ_PLAYERS_URI = URI.create("https://pmaapi.premierleague.com/api/External/v1.0/Players/playerId");
    private final static URI PMA_INJ_PLAYERS_STATUS_URI = URI.create("https://pmaapi.premierleague.com/api/External/Teams/teamId/PlayerStatus/id");


    private final String pmaPlayerInjUserName;
    private final String pmaPlayerInjPassword;
    private final String pmaPlayerInjClientId;
    private final String pmaPlayerInjApiKey;
    private final String pmaPlayerInjAppName;
    private RestTemplate restTemplate;

    @Autowired
    public PmaPlayerRestService(RestTemplate restTemplate,
                                @Value("${pma.username}") String pmaPlayerInjUserName,
                                @Value("${pma.password}") String pmaPlayerInjPassword,
                                @Value("${pma.client-id}") String pmaPlayerInjClientId,
                                @Value("${pma.app-name}") String pmaPlayerInjAppName,
                                @Value("${pma.api-key}") String pmaPlayerInjApiKey) {


        this.restTemplate = restTemplate;
        this.pmaPlayerInjUserName = pmaPlayerInjUserName;
        this.pmaPlayerInjPassword = pmaPlayerInjPassword;
        this.pmaPlayerInjClientId = pmaPlayerInjClientId;
        this.pmaPlayerInjAppName = pmaPlayerInjAppName;
        this.pmaPlayerInjApiKey = pmaPlayerInjApiKey;

    }

    @Async
    public CompletableFuture<PmaPlayerInjuryTeamDTO> getPlayerTeams(String playerExternalId) {

        return getAuthToken().thenComposeAsync(token -> requestPlayerTeamId(token, playerExternalId));
    }

    private CompletableFuture<PmaPlayerInjuryTeamDTO> requestPlayerTeamId(String token, String playerExternalId) {
        return CompletableFuture.supplyAsync(() -> {

            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();

            param.add("playerId", playerExternalId);

            URI uriWithParams = UriComponentsBuilder.fromUri(PMA_INJ_PLAYERS_URI).queryParams(param).build().toUri();

            ResponseEntity<PmaPlayerInjuryTeamDTO> response = restTemplate.exchange(uriWithParams,
                    HttpMethod.GET, buildRequest(token), PmaPlayerInjuryTeamDTO.class);
            return Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new RuntimeException(response.getStatusCodeValue() + "  " + uriWithParams));
        });

    }

    @Async
    public CompletableFuture<PmaPlayerInjuryStatusGroupDTO> getPlayerStatus(Integer teamId, String playerExternalId) {

        return getAuthToken().thenComposeAsync(token -> requestPlayerStatus(token, teamId, playerExternalId));
    }

    private CompletableFuture<PmaPlayerInjuryStatusGroupDTO> requestPlayerStatus(String token, Integer teamId, String playerExternalId) {

        return CompletableFuture.supplyAsync(() -> {


            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("teamId", teamId.toString());
            params.add("id", playerExternalId);

            URI uriWithParams = UriComponentsBuilder.fromUri(PMA_INJ_PLAYERS_STATUS_URI).queryParams(params).build().toUri();
            ResponseEntity<PmaPlayerInjuryStatusGroupDTO> response = restTemplate.exchange(uriWithParams,
                    HttpMethod.GET, buildRequest(token), PmaPlayerInjuryStatusGroupDTO.class);


            return Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new RuntimeException(response.getStatusCodeValue() + " " + uriWithParams));
        });

    }


    private CompletableFuture<String> getAuthToken() {
        {

            return CompletableFuture.supplyAsync(() -> {

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                httpHeaders.set("api_key", pmaPlayerInjApiKey);
                httpHeaders.set("appName", pmaPlayerInjAppName);

                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                map.add("grant_type", "password");
                map.add("username", pmaPlayerInjUserName);
                map.add("password", pmaPlayerInjPassword);
                map.add("client_id", pmaPlayerInjClientId);

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpHeaders);

                ResponseEntity<PmaPlayerInjAuthTokenDTO> response = restTemplate.postForEntity(PMA_INJ_PLAYERS_AUTH_URI, request, PmaPlayerInjAuthTokenDTO.class);


                return Optional.ofNullable(response.getBody())
                        .map(PmaPlayerInjAuthTokenDTO::getAccessToken)
                        .orElseThrow(() -> new RuntimeException(response.getStatusCodeValue() + " " + PMA_INJ_PLAYERS_AUTH_URI.toString()));
            });
        }
    }


    private HttpEntity<Object> buildRequest(String token) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        httpHeaders.set("apiKey", pmaPlayerInjApiKey);
        httpHeaders.set("appName", pmaPlayerInjAppName);

        return new HttpEntity<>(httpHeaders);
    }

}
