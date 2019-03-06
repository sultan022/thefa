package com.thefa.audit.model.dto.foreign;

import com.thefa.audit.model.shared.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor
public class ForeignPlayerDTO {

    private String foreignPlayerId;

    private String firstName;

    private String lastName;

    private String gender;

    private String profileImage;

    private LocalDate dateOfBirth;

    private String clubName;

    private String nationality;

    private DataSourceType source;

    private Boolean existsPPS;
}
