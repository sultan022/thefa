package com.thefa.audit.rest;

import com.thefa.audit.model.dto.foreign.ForeignPlayerLookupDTO;
import com.thefa.audit.model.dto.rest.fan.FanServiceAuthDTO;
import com.thefa.audit.model.dto.rest.fan.FanServicePlayerDTO;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@CommonsLog
public class FanIdService {

    private static final String AUTH_TOKEN_URI = "/v1/api/oauth/token";
    private static final String PLAYERS_URI = "/v1/api/Players";

    private final String serviceHost;
    private final String serviceUsername;
    private final String servicePassword;

    private final RestTemplate restTemplate;

    @Autowired
    public FanIdService(@Value("${fan.lookup.proxy.host}") String proxyHost,
                        @Value("${fan.lookup.proxy.port}") Integer proxyPort,
                        @Value("${fan.lookup.service.host}") String serviceHost,
                        @Value("${fan.lookup.service.username}") String serviceUsername,
                        @Value("${fan.lookup.service.password}") String servicePassword) {

        this.serviceHost = serviceHost;
        this.serviceUsername = serviceUsername;
        this.servicePassword = new String(servicePassword.getBytes(ISO_8859_1), UTF_8);

        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        simpleClientHttpRequestFactory.setProxy(proxy);

        this.restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
    }

    public CompletableFuture<List<FanServicePlayerDTO>> findPlayers(@NonNull ForeignPlayerLookupDTO foreignPlayerLookupDTO) {

        return getAuthToken()
                .thenComposeAsync(token -> findPlayers(token, foreignPlayerLookupDTO));
    }

    private CompletableFuture<List<FanServicePlayerDTO>> findPlayers(String token, @NonNull ForeignPlayerLookupDTO foreignPlayerLookupDTO) {
        return CompletableFuture.supplyAsync(() -> {

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            Optional.ofNullable(foreignPlayerLookupDTO.getFirstName()).ifPresent(firstName -> params.add("FirstName", firstName));
            Optional.ofNullable(foreignPlayerLookupDTO.getLastName()).ifPresent(lastName -> params.add("LastName", lastName));
            Optional.ofNullable(foreignPlayerLookupDTO.getDateOfBirth()).ifPresent(dob -> params.add("DateOfBirth", dob.toString()));

            URI uriWithParams = UriComponentsBuilder.fromHttpUrl(serviceHost + PLAYERS_URI).queryParams(params).build().toUri();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);

            try {
                ResponseEntity<List<FanServicePlayerDTO>> response = restTemplate.exchange(uriWithParams, HttpMethod.GET,
                        new HttpEntity<>(httpHeaders),
                        new ParameterizedTypeReference<List<FanServicePlayerDTO>>() {});


                return Optional.ofNullable(response.getBody())
                        .<RuntimeException>orElseThrow(() -> new RuntimeException(response.getStatusCodeValue() + " " + uriWithParams.toString()));

            } catch (HttpClientErrorException e) {

                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Fan Id not found for search request: " + foreignPlayerLookupDTO);
                    return new ArrayList<>();
                }

                throw new RuntimeException(e);
            }

        });
    }


    private CompletableFuture<String> getAuthToken() {

        return CompletableFuture.supplyAsync(() -> {

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "password");
            map.add("username", serviceUsername);
            map.add("password", servicePassword);

            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, httpHeaders);

            ResponseEntity<FanServiceAuthDTO> response = restTemplate.postForEntity(serviceHost + AUTH_TOKEN_URI, httpEntity, FanServiceAuthDTO.class);

            return Optional.ofNullable(response.getBody())
                    .map(FanServiceAuthDTO::getAccessToken)
                    .<RuntimeException>orElseThrow(() -> new RuntimeException(response.getStatusCodeValue() + " " + AUTH_TOKEN_URI));
        });

    }

}
