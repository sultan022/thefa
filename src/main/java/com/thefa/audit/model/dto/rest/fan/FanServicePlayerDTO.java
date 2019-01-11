package com.thefa.audit.model.dto.rest.fan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FanServicePlayerDTO {

    @JsonProperty("IndividualId")
    private Long individualId;

    @JsonProperty("FanCode")
    private Long fanId;

    @JsonProperty("FirstName")
    private String firstName;

    @JsonProperty("MiddleName")
    private String middleName;

    @JsonProperty("LastName")
    private String lastName;

    @JsonProperty("KnownAs")
    private String knownAs;

    @JsonProperty("DateOfBirth")
    private String dateOfBirth;

    @JsonProperty("PlaceOfBirth")
    private String placeOfBirth;

    @JsonProperty("Organisation")
    private String organisation;

    @JsonProperty("PlayerStatus")
    private String playerStatus;

    @JsonProperty("RegistrationDate")
    private String registrationDate;

    @JsonProperty("ExpiryDate")
    private String expiryDate;

    @JsonProperty("RegistrationId")
    private Long registrationId;

    @JsonProperty("Gender")
    private String gender;

}
