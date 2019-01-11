package com.thefa.audit.model.dto.rest.fan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FanServiceAuthDTO {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private long expiresIn;

}
