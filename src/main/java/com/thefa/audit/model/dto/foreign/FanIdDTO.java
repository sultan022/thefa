package com.thefa.audit.model.dto.foreign;

import com.thefa.audit.model.shared.DataSourceType;
import com.thefa.common.dto.shared.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FanIdDTO {

    private Long fanId;

    private String firstName;

    private String middleName;

    private String lastName;

    private String knownAs;

    private LocalDate dateOfBirth;

    private String placeOfBirth;

    private String organisation;

    private String playerStatus;

    private LocalDate registrationDate;

    private LocalDate expiryDate;

    private Long registrationId;

    private Gender gender;

    private String profileImage;

    public ForeignPlayerDTO toForeignPlayerDTO() {
        ForeignPlayerDTO foreignPlayerDTO = new ForeignPlayerDTO();

        foreignPlayerDTO.setForeignPlayerId(fanId == null ? null : String.valueOf(fanId));
        foreignPlayerDTO.setFirstName(firstName);
        foreignPlayerDTO.setLastName(lastName);
        foreignPlayerDTO.setDateOfBirth(dateOfBirth);
        foreignPlayerDTO.setGender(gender == null ? null : gender.name());
        foreignPlayerDTO.setSource(DataSourceType.FAN);

        return foreignPlayerDTO;
    }

}
