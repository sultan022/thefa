package com.thefa.audit.model.dto.foreign;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor
public class ForeignPlayerLookupDTO {

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    @JsonIgnore
    public final boolean isEmpty() {
        return firstName == null && lastName == null && dateOfBirth == null;
    }

}
